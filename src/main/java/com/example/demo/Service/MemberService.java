package com.example.demo.Service;

import com.example.demo.config.SecurityUtil;
import com.example.demo.dto.auth.syncTokenResponseDto;
import com.example.demo.dto.member.FriendRequestDto;
import com.example.demo.dto.member.FriendRequestReplyDto;
import com.example.demo.dto.member.FriendrequestResponseDto;
import com.example.demo.dto.member.MemberResponseDto;
import com.example.demo.entity.DeviceToken;
import com.example.demo.entity.Member.Friendship;
import com.example.demo.entity.Member.Member;
import com.example.demo.repository.DeviceTokenRepository;
import com.example.demo.repository.Member.FriendshipRepository;
import com.example.demo.repository.Member.MemberRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final NicknameGenerator nicknameGenerator;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FriendshipRepository friendshipRepository;

    public MemberResponseDto getMyInfoBySecurity() {
        return memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
    }

    @Transactional
    public MemberResponseDto changeMemberNickname() {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
        member.setNickname(nicknameGenerator.generateRandomNickname());
        return MemberResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public MemberResponseDto changeMemberPassword(String email, String exPassword, String newPassword) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
        if (!passwordEncoder.matches(exPassword, member.getPassword())) {
            throw new RuntimeException("비밀번호가 맞지 않습니다");
        }
        member.setPassword(passwordEncoder.encode((newPassword)));
        return MemberResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public void handleFriendRequestReply(FriendRequestReplyDto reply) {
        Friendship request = friendshipRepository.findById(reply.getRequestId()).orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없음"));
        Member replyer = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));

        if(!request.getAddresseeEmail().equals(replyer.getEmail())) {
            throw new IllegalStateException("해당 친구 요청을 받을 권한이 없습니다.");
        }

        if(request.getStatus()!= Friendship.Status.PENDING){
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        if(request.getStatus()== Friendship.Status.ACCEPTED){
            request.setStatus(Friendship.Status.ACCEPTED);
            friendshipRepository.save(request);
        }else if(request.getStatus()== Friendship.Status.REJECTED){
            request.setStatus(Friendship.Status.REJECTED);
            friendshipRepository.save(request);
        }

    }

    @Transactional
    public FriendrequestResponseDto sendFriendRequest(FriendRequestDto dto) {
        Member requester = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElseThrow(()-> new RuntimeException("로그인 유저 정보가 없습니다."));

        List<Friendship> existingRequests = friendshipRepository
                .findByAddresseeEmailAndStatus(dto.getEmailToRequest(), Friendship.Status.PENDING);

        boolean alreadyRequested = existingRequests.stream()
                .anyMatch(f -> f.getRequester().getId().equals(requester.getId()));

        // 2. 이미 친구 상태인지 확인
        List<Friendship> acceptedFriends = friendshipRepository
                .findByAddresseeEmailAndStatus(dto.getEmailToRequest(), Friendship.Status.ACCEPTED);

        boolean alreadyFriends = acceptedFriends.stream()
                .anyMatch(f -> f.getRequester().getId().equals(requester.getId()));

        if (alreadyRequested || alreadyFriends) {
            throw new IllegalStateException("이미 요청을 보냈거나 친구입니다.");
        }


        // 중복 요청 방지 로직도 추가 가능
        Friendship friendship = Friendship.builder()
                .requester(requester)
                .email(dto.getEmailToRequest())
                .status(Friendship.Status.PENDING)
                .build();
        try {
            friendshipRepository.save(friendship);
            return FriendrequestResponseDto.builder().requested(true).build();
        }catch (Exception e){
            e.printStackTrace();
            return FriendrequestResponseDto.builder().requested(false).build();
        }


    }

    public List<Friendship> getPendingRequestsForUser(){
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));
        String myEmail = member.getEmail();
        return friendshipRepository.findByAddresseeEmailAndStatus(myEmail, Friendship.Status.PENDING);
    }


    //apns 토큰 등록
    public syncTokenResponseDto syncToken(String token) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));


        // 멤버ID가 이미 등록이 된 동일한 토큰이 있는지 확인
        Optional<DeviceToken> deviceToken = deviceTokenRepository.findByTokenAndMemberIdIsNotNull(token);

        //이미 있으면
        if (deviceToken.isPresent()) {
            DeviceToken temp = deviceToken.get();
            temp.setMember(member);
            deviceTokenRepository.save(temp);
            return syncTokenResponseDto.builder()
                    .success(true)
                    .build();
        //아니라면
        } else {
            //멤버ID가 없는 동일한 토큰이 있는지 확인
            deviceTokenRepository.findAllByMemberIdIsNullAndTokenEquals(token).forEach(Token -> {
                Token.setMember(member);
                Token.setIsRegistered(true);

            });

            return syncTokenResponseDto.builder()
                    .success(true)
                    .build();
        }

    }
}