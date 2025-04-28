package com.example.demo.dto.location;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 친구 위치 응답 목록의 각 항목을 나타내는 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationReplyDto {
    private String userId; // 친구의 유저 ID (String)
    private double latitude;
    private double longitude;
    // 필요하다면 private String nickname; 등 추가 필드 가능
}