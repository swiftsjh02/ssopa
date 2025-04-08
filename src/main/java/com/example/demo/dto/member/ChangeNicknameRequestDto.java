package com.example.demo.dto.member;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ChangeNicknameRequestDto {    private String Nickname;    private String email;
}