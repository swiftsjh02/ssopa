package com.example.demo.Controller;



import com.example.demo.common.HttpResponseUtil;
import com.example.demo.dto.push.newPushTokenDto;
import lombok.RequiredArgsConstructor;
import com.example.demo.Service.PushNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;
    private final HttpResponseUtil httpResponseUtil;


    /**
     * 회원의 디바이스 토큰을 등록 또는 갱신하는 API.
     * 예시: POST /api/push/token/register?memberId=1&token=abc123
     *
     * @return 처리 결과 메시지
     */
    @PostMapping("/token/register")
    public ResponseEntity<?> registerToken(@RequestBody newPushTokenDto dto) {

        try{
            return httpResponseUtil.createOKHttpResponse(pushNotificationService.registerOrUpdateDeviceToken(dto),"ok");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse(e.getMessage());
        }

    }


    /**
     * 회원의 토큰을 사용하여 푸시 알림을 전송하는 API.
     * 예시: POST /api/push/send?memberId=1&title=안녕하세요&body=메시지 내용
     *
     * @param memberId 회원 식별자
     * @param title    알림 제목
     * @param body     알림 본문
     * @return 전송 결과 메시지
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestParam Long memberId,
                                                   @RequestParam String title,
                                                   @RequestParam String body) {

        try{
            return httpResponseUtil.createOKHttpResponse(pushNotificationService.sendNotification(memberId, title, body),"ok");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse(e.getMessage());
        }


    }
}




