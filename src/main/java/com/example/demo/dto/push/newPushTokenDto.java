package com.example.demo.dto.push;

import com.example.demo.entity.DeviceToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class newPushTokenDto {
    private String token;
}


