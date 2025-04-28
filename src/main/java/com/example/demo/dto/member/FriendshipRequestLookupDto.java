package com.example.demo.dto.member;


import com.example.demo.entity.Member.Friendship;
import lombok.Getter;


@Getter
public class FriendshipRequestLookupDto {
    private Long id;
    private String requesterName;
    private String requesterEmail;
    private String addresseeEmail;
    private String status;

    public FriendshipRequestLookupDto(Friendship friendship) {
        this.id = friendship.getId();
        this.requesterName = friendship.getRequester().getName(); // 필요하면
        this.requesterEmail = friendship.getRequester().getEmail();
        this.addresseeEmail = friendship.getAddresseeEmail();
        this.status = friendship.getStatus().name();
    }
}

