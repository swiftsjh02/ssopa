package com.example.demo.dto.jwt;


import io.swagger.v3.oas.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenReqDto {    private String accessToken;    private String refreshToken;
}