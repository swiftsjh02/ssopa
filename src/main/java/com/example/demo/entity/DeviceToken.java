package com.example.demo.entity;

import com.example.demo.entity.Member.Member;
import lombok.*;

import javax.persistence.*;


@Getter //lombok
@Setter
@Entity
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;


    @Column(nullable = false)
    private Boolean isRegistered;

    @OneToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id", nullable = true)
    private Member memberId;

    public void setMember(Member member) {
        this.memberId = member;
    }
}
