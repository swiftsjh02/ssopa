package com.example.demo.Controller.Chat;

import com.example.demo.dto.chat.ChatMessage;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;

    private final MemberRepository memberRepository;

    @MessageMapping("/chat/message")
    public void enter(Principal principal,ChatMessage message) {


        if (principal == null) {
            // StompHandler에서 인증을 강제한다면 이 경우는 거의 없음
            System.out.println("인증되지 않은 사용자로부터 메시지 수신!");
            return; // 또는 예외 처리
        }


        String sender = principal.getName(); // Principal에서 사용자 이름(여기서는 db의 member primary atrribute) 가져오기


        Optional<Member> sender_tmp  =memberRepository.findById(Long.valueOf(sender));
        sender = sender_tmp.get().getNickname();
        message.setSender(sender);

        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(sender + "님이 입장하였습니다.");
            //message.setTime(LocalDateTime.now());
        }

        //message.setTime(LocalDateTime.now());
        sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }
}//