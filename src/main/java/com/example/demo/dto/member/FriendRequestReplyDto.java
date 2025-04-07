package com.example.demo.dto.member;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FriendRequestReplyDto {
    private Long requestId;
    private Boolean isAccepted;
}
