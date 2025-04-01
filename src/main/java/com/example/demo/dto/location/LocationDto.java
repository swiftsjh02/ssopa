package com.example.demo.dto.location;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



//유저가 본인의 위치를 서버로 전송할때 쓰는 dto

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private double latitude;
    private double longitude;
}