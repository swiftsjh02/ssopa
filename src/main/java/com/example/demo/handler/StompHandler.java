package com.example.demo.handler;

import com.example.demo.dto.location.LocationDto; // LocationDto import
import com.example.demo.dto.chat.ChatMessage;
import com.example.demo.entity.Location;
import com.example.demo.jwt.TokenProvider; // JWT Provider 클래스 이름 확인
import com.example.demo.repository.Chat.ChatMessageRepository;
// import com.example.demo.repository.Member.MemberRepository; // MemberRepository 사용 여부 확인
import com.example.demo.repository.LocationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException; // 또는 다른 적절한 예외
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@Slf4j // 로깅 추가 권장
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider jwtService; // 또는 jwtTokenProvider
    private final ChatMessageRepository chatMessageRepository;
    private final LocationRepository locationRepository;
    private final ObjectMapper objectMapper; // ObjectMapper 주입 (Spring Boot가 자동 설정 가능)

    private static final String BEARER_PREFIX = "Bearer ";

    // 목적지 상수 정의
    private static final String CHAT_DESTINATION_PREFIX = "/app/chat"; // 예시: 채팅 메시지 목적지 접두사
    private static final String LOCATION_DESTINATION = "/app/location/update"; // 위치 업데이트 목적지


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            log.error("StompHeaderAccessor is null, cannot process message.");
            // 적절한 예외 처리 또는 로깅 후 메시지 반환/null 반환
            return message; // 또는 throw new IllegalStateException("Accessor cannot be null");
        }

        log.debug("STOMP Command: {}, Destination: {}", accessor.getCommand(), accessor.getDestination());

        try {
            switch (Objects.requireNonNull(accessor.getCommand())) {
                case CONNECT:
                    // 연결 시 인증 처리
                    handleConnect(accessor);
                    break;
                case SEND:
                    // 메시지 전송 시 목적지에 따른 처리
                    handleSend(accessor, message);
                    break;
                case SUBSCRIBE:
                    // 구독 요청 시 로직 (필요하다면)
                    handleSubscribe(accessor);
                    break;
                case DISCONNECT:
                    // 연결 종료 시 로직 (필요하다면)
                    handleDisconnect(accessor);
                    break;
                default:
                    // 다른 명령어 처리 (필요하다면)
                    break;
            }
        } catch (AuthenticationException | AccessDeniedException e) {
            log.error("Authentication/Authorization error processing STOMP message: {}", e.getMessage());
            // 인증/인가 실패 시 연결/메시지 처리 중단 (예외 전파)
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing STOMP message: {}", e.getMessage(), e);
            // 예상치 못한 오류 로깅, 필요에 따라 메시지 처리 중단 (null 반환 또는 예외 전파)
            // throw new MessageDeliveryException("Failed to process message", e);
            return null; // 메시지 처리 중단
        }

        return message; // 처리가 성공적으로 완료되면 메시지 반환
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String authorizationHeader = accessor.getFirstNativeHeader("Authorization"); // Native 헤더에서 직접 가져오기
        log.debug("CONNECT Authorization header: {}", authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            log.warn("CONNECT request without valid Bearer token.");
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (!jwtService.validateToken(token)) { // validateToken 구현 확인 필요
            log.warn("Invalid JWT token received during CONNECT.");
            throw new BadCredentialsException("Invalid JWT token");
        }

        // 토큰에서 사용자 ID (DB에 member의 primary number) 추출
        String userId = jwtService.getMemberIdByToken(token); // 이 메서드가 Email을 반환한다고 가정
        if (userId == null) {
            log.error("Failed to extract user ID from valid token.");
            throw new InternalAuthenticationServiceException("Could not extract user ID from token");
        }

        log.info("User '{}' connected via WebSocket.", userId);
        // STOMP 세션에 인증된 사용자 정보 설정 (매우 중요!)
        Principal userPrincipal = new UsernamePasswordAuthenticationToken(userId, null, null); // 간단한 Principal 객체
        accessor.setUser(userPrincipal);
    }

    private void handleSend(StompHeaderAccessor accessor, Message<?> message) {
        Principal principal = accessor.getUser();

        // SEND 요청 시 사용자 인증 확인 (CONNECT에서 설정된 Principal 사용)
        if (principal == null || principal.getName() == null) {
            log.warn("SEND request received without authenticated user principal for destination: {}", accessor.getDestination());
            throw new InternalAuthenticationServiceException("Could not extract user ID from token");
        }

        String userId = principal.getName(); // 인증된 사용자 ID (Email)
        String destination = accessor.getDestination();

        if (destination == null) {
            log.warn("SEND request received with null destination from user '{}'", userId);
            // 목적지가 없는 메시지는 처리하지 않음 (또는 에러 처리)
            return; // 또는 throw new MessageDeliveryException("Destination is required");
        }

        // 목적지에 따라 분기 처리
        if (destination.startsWith(CHAT_DESTINATION_PREFIX)) {
            // 채팅 메시지 처리
            log.debug("Processing chat message for user '{}' to destination '{}'", userId, destination);
            processChatMessage(message, userId);
        } else if (destination.equals(LOCATION_DESTINATION)) {
            // 위치 정보 업데이트 처리
            log.debug("Processing location update for user '{}'", userId);
            processLocationUpdate(message, userId);
        } else {
            // 알 수 없거나 처리하지 않는 목적지
            log.warn("Received message for unhandled destination '{}' from user '{}'", destination, userId);
        }

        // 필요 시: 원본 메시지 헤더에 발신자 정보 추가 (하지만 Principal 사용하는 것이 더 표준적)
        // accessor.setNativeHeader("sender", userId); // 기존 로직 유지 필요 시
    }

    private void processChatMessage(Message<?> message, String userId) {
        byte[] payload = (byte[]) message.getPayload();
        String messageContent = new String(payload, StandardCharsets.UTF_8);
        log.debug("Chat message payload: {}", messageContent);

        try {
            JsonNode jsonNode = objectMapper.readTree(messageContent);

            // roomId와 message 필드가 있는지, null이 아닌지 확인
            JsonNode roomIdNode = jsonNode.get("roomId");
            JsonNode messageNode = jsonNode.get("message");

            if (roomIdNode == null || roomIdNode.isNull() || !roomIdNode.isTextual()) {
                log.warn("Invalid chat message payload: 'roomId' is missing, null, or not text.");
                return; // 또는 예외 처리
            }
            if (messageNode == null || messageNode.isNull() || !messageNode.isTextual()) {
                log.warn("Invalid chat message payload: 'message' is missing, null, or not text.");
                return; // 또는 예외 처리
            }

            String roomId = roomIdNode.asText();
            String content = messageNode.asText();

            // ChatMessage 객체 생성 및 저장
            ChatMessage chatMessage = new ChatMessage(ChatMessage.MessageType.TALK, roomId, userId, content);
            chatMessageRepository.save(chatMessage);
            log.info("Saved chat message from user '{}' for room '{}'", userId, roomId);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse chat message JSON payload for user '{}': {}", userId, e.getMessage());
            // throw new MessageDeliveryException("Invalid JSON payload for chat message", e); // 필요 시 예외 전파
        } catch (Exception e) {
            log.error("Failed to save chat message for user '{}': {}", userId, e.getMessage(), e);
            // throw new MessageDeliveryException("Failed to save chat message", e); // 필요 시 예외 전파
        }
    }

    // processLocationUpdate 메서드 수정
    private void processLocationUpdate(Message<?> message, String userId) {
        byte[] payload = (byte[]) message.getPayload();
        String jsonPayload = new String(payload, StandardCharsets.UTF_8);
        log.debug("Location update payload: {}", jsonPayload);

        try {
            // 클라이언트로부터 받은 JSON을 LocationDto로 변환
            LocationDto locationDto = objectMapper.readValue(jsonPayload, LocationDto.class);


            // Redis에 저장할 Location 엔티티 생성 (userId 포함)
            Location locationToSave = new Location(userId, locationDto.getLatitude(), locationDto.getLongitude());

            // LocationRepository를 사용하여 저장 (save 호출 시 TTL 자동 적용됨)
            locationRepository.save(locationToSave);

            log.info("User '{}' location updated: [{}, {}]. Saved to Redis with keyspace '{}'. TTL applied.",
                    userId, locationToSave.getLatitude(), locationToSave.getLongitude(), "userloc"); // @RedisHash의 value

        } catch (JsonProcessingException e) {
            log.error("Failed to parse location update JSON payload for user '{}': {}", userId, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to save location update for user '{}' using Repository: {}", userId, e.getMessage(), e);
        }
    }

    // ... isValidLatitude, isValidLongitude, handleSubscribe, handleDisconnect 등 ...

    private void handleSubscribe(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal != null) {
            log.info("User '{}' subscribing to destination: {}", principal.getName(), accessor.getDestination());
            // 필요 시 구독 권한 검사 등 로직 추가
        } else {
            log.warn("Anonymous user attempting to subscribe to: {}", accessor.getDestination());
            // throw new AccessDeniedException("Subscription requires authentication");
        }
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        Optional.ofNullable(accessor.getUser())
                .map(Principal::getName)
                .ifPresent(userId -> {
                    log.info("User '{}' disconnected.", userId);
                    // 선택 사항: 연결 종료 시 Redis에서 위치 정보 즉시 삭제
                    // String redisKey = USER_LOCATION_REDIS_PREFIX + userId;
                    // Boolean deleted = redisTemplate.delete(redisKey);
                    // log.info("Location data for user {} deleted from Redis upon disconnect: {}", userId, deleted);
                });
    }


}
