package com.example.demo.dto.auth;

import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequestResponseDto {
    private String userEmail;
    private double latitude;
    private double longitude;
}



