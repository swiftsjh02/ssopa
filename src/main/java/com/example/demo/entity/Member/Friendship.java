package com.example.demo.entity.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private Member requester;

    @Column(nullable = false)
    private String addresseeEmail;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }


    @Builder
    public Friendship(Long id, Member requester, String email,Friendship.Status status) {
        this.id = id;
        this.requester = requester;
        this.addresseeEmail = email;
        this.status = status;
    }
}
