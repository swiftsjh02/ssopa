package com.example.demo.Controller.Location;

import com.example.demo.Service.MemberService; // MemberService 주입을 위해 import
import com.example.demo.dto.location.LocationReplyDto;
import com.example.demo.entity.Location;
import com.example.demo.entity.Member.Member; // Member 엔티티 import
import com.example.demo.repository.LocationRepository;
// import com.example.demo.repository.Member.MemberRepository; // LocationController에서는 MemberService를 통해 접근
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LocationController {

    private final SimpMessageSendingOperations sendingOperations;
    private final LocationRepository locationRepository;
    private final MemberService memberService; // MemberService 주입

    // 클라이언트가 친구 위치를 요청하는 메시지를 보낼 목적지
    private static final String FRIEND_LOCATION_REQUEST_DESTINATION = "/location/requestFriends";

    // 서버가 클라이언트에게 친구 위치 목록을 응답할 목적지 (유저별 고유 토픽)
    // /user/{userId}/queue/friendsLocationResponse 와 같은 형태가 됩니다.
    private static final String FRIEND_LOCATION_RESPONSE_TOPIC = "/queue/friendsLocationResponse";


    // processLocationUpdate 메서드는 StompHandler에 있다고 가정합니다.
    // 만약 LocationController에서 처리한다면 여기에 구현하고 StompHandler에서 제거해야 합니다.


    /**
     * 클라이언트로부터 친구 위치 요청 메시지를 처리합니다.
     * 요청 메시지는 페이로드가 비어있거나 간단한 내용일 수 있으며,
     * 중요한 것은 Principal을 통해 요청한 유저를 식별하는 것입니다.
     *
     * @param principal 요청한 유저의 Principal (StompHandler에서 설정됨)
     * // @param payload   요청 메시지의 페이로드 (필요에 따라 사용) <-- 이 인자를 제거했습니다.
     */
    @MessageMapping(FRIEND_LOCATION_REQUEST_DESTINATION)
    // public void handleFriendLocationRequest(Principal principal, String payload) { // 기존 시그니처
    public void handleFriendLocationRequest(Principal principal) { // <-- 수정된 시그니처
        if (principal == null || principal.getName() == null) {
            log.warn("Received friend location request from unauthenticated user.");
            // 인증되지 않은 사용자는 처리하지 않음 (StompHandler에서 이미 막혔을 가능성이 높지만 방어 코드)
            return;
        }

        String requestingUserIdString = principal.getName(); // 요청한 유저의 ID (String 형태, Member.id를 String으로 변환한 값)
        log.info("Received friend location request from user: {}", requestingUserIdString);

        List<LocationReplyDto> friendLocations = new ArrayList<>();

        try {
            // 1. 요청한 유저의 친구 Member 객체 목록을 가져옵니다.
            List<Member> friends = memberService.getFriendsByUserId(requestingUserIdString);

            if (friends.isEmpty()) {
                log.info("User {} has no friends or failed to retrieve friend list.", requestingUserIdString);
                // 친구가 없더라도 빈 목록을 응답으로 보낼 수 있습니다.
                sendFriendLocationsToUser(requestingUserIdString, Collections.emptyList());
                return;
            }

            // 2. 각 친구의 현재 위치를 Redis에서 조회합니다.
            for (Member friend : friends) {
                // 친구 Member 객체의 ID(Long)를 String으로 변환하여 Redis 키로 사용
                String friendIdString = String.valueOf(friend.getId());

                // Location 엔티티의 ID가 유저 ID(String)와 동일하다고 가정합니다.
                Optional<Location> friendLocation = locationRepository.findById(friendIdString);

                friendLocation.ifPresent(loc -> {
                    // 응답 DTO에 친구 ID(String)와 위치 정보를 담습니다.
                    // 필요하다면 여기서 친구의 닉네임 등 추가 정보도 DTO에 담을 수 있습니다.
                    friendLocations.add(new LocationReplyDto(loc.getUserId(), loc.getLatitude(), loc.getLongitude()));
                });
            }

            log.info("Found {} friend locations for user {}", friendLocations.size(), requestingUserIdString);

            // 3. 조회된 친구 위치 목록을 요청한 유저에게만 응답으로 보냅니다.
            sendFriendLocationsToUser(requestingUserIdString, friendLocations);

        } catch (IllegalArgumentException e) {
            log.error("Invalid user ID or friend lookup error for user {}: {}", requestingUserIdString, e.getMessage());
            // 클라이언트에게 오류 응답을 보낼 수도 있습니다.
        } catch (Exception e) {
            log.error("Error processing friend location request for user {}: {}", requestingUserIdString, e.getMessage(), e);
            // 클라이언트에게 오류 응답을 보낼 수도 있습니다.
        }
    }

    /**
     * 특정 유저에게 친구 위치 목록 응답 메시지를 전송합니다.
     *
     * @param userIdString    응답을 받을 유저 ID (String)
     * @param friendLocations 전송할 친구 위치 목록 DTO
     */
    private void sendFriendLocationsToUser(String userIdString, List<LocationReplyDto> friendLocations) {
        // /user/{userIdString}/queue/friendsLocationResponse 토픽으로 메시지를 보냅니다.
        // Spring은 /user/ 접두사를 보고 해당 세션으로만 메시지를 라우팅합니다.
        sendingOperations.convertAndSendToUser(
                userIdString, // 메시지를 받을 유저의 Principal Name (여기서는 userIdString)
                FRIEND_LOCATION_RESPONSE_TOPIC, // 유저별 토픽의 나머지 부분
                friendLocations // 전송할 데이터 (List<FriendLocationItemDto>)
        );
        log.info("Sent friend location response to user {} with {} locations.", userIdString, friendLocations.size());
    }
}