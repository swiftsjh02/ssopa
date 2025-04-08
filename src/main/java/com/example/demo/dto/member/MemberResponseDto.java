package com.example.demo.dto.member;

import com.example.demo.entity.Member.Authority;
import com.example.demo.entity.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {    private String email;    private String profileImage;    private String nickname;    private Authority authority;
    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .profileImage(member.getProfileImage())
                .nickname(member.getNickname())
                .authority(member.getAuthority())
                .build();
    }
}