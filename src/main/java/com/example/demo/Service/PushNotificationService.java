package com.example.demo.Service;

import com.example.demo.config.SecurityUtil;
import com.example.demo.dto.push.newPushTokenDto;
import com.example.demo.dto.push.newPushTokenResponseDto;
import com.example.demo.entity.DeviceToken;
import com.example.demo.entity.Member.Member;
import com.example.demo.repository.DeviceTokenRepository;
import com.example.demo.repository.Member.MemberRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public PushNotificationService(DeviceTokenRepository deviceTokenRepository, MemberRepository memberRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 회원의 디바이스 토큰을 등록 또는 갱신합니다.
     *
     * @return 처리 결과 메시지
     */
    public newPushTokenResponseDto registerOrUpdateDeviceToken(newPushTokenDto dto) {
        // memberId로 회원 조회
        Member memberId = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다"));

        // 해당 회원의 토큰이 이미 등록되어 있는지 확인
        Optional<DeviceToken> optionalDeviceToken = deviceTokenRepository.findById(memberId.getId());
        if (optionalDeviceToken.isPresent()) {
            // 토큰이 존재하면 갱신
            DeviceToken deviceToken = optionalDeviceToken.get();
            deviceToken.setToken(dto.getToken());
            deviceToken.setIsRegistered(true);
            deviceTokenRepository.save(deviceToken);
            return new newPushTokenResponseDto(Boolean.TRUE);
        } else {
            // 토큰이 없으면 신규 등록
            DeviceToken deviceToken = DeviceToken.builder()
                    .token(dto.getToken())
                    .isRegistered(true)
                    .memberId(memberId)
                    .build();
            deviceTokenRepository.save(deviceToken);
            return new newPushTokenResponseDto(Boolean.TRUE);
        }

    }

    /**
     * 해당 회원의 등록된 디바이스 토큰을 이용해 푸시 알림 메시지를 전송합니다.
     *
     * @param memberId 회원 식별자
     * @param title    알림 제목
     * @param body     알림 본문
     * @return 전송 결과 메시지
     */
    public String sendNotification(Long memberId, String title, String body) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 회원에 등록된 토큰 조회
        Optional<DeviceToken> optionalDeviceToken = deviceTokenRepository.findByMemberId(member);
        if (!optionalDeviceToken.isPresent()) {
            return "회원에 등록된 토큰이 없습니다.";
        }
        String token = optionalDeviceToken.get().getToken();

        // Firebase Notification 생성
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // 메시지 구성 (토큰 기반)
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            return "알림 전송 성공, 메시지 ID: " + response;
        } catch (Exception e) {
            e.printStackTrace();
            return "알림 전송 실패: " + e.getMessage();
        }
    }
}
