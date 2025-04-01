package com.example.demo.entity;

import lombok.*; // NoArgsConstructor, AllArgsConstructor, Setter 추가
import org.springframework.data.annotation.Id; // @Id import
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed; // 필요 시 인덱싱용

import java.io.Serializable; // 직렬화 추가 (권장)

@Getter
@Setter // Setter 추가 (또는 생성자로만 값 설정)
@NoArgsConstructor // Redis 매핑 시 필요할 수 있음
@AllArgsConstructor // 모든 필드를 받는 생성자 추가
@ToString // 디버깅용
@RedisHash(value = "userloc", timeToLive = 60 * 5) // keyspace 변경 (location -> userloc), TTL 5분
public class Location implements Serializable { // Serializable 구현

    private static final long serialVersionUID = 1L; // Serializable 버전 ID

    @Id // 이 필드가 Redis 키의 ID 부분이 됨 (예: userloc:someUserId)
    private String userId; // 사용자 ID를 저장할 필드 추가

    // 필요하다면 위도/경도로 검색하기 위해 @Indexed 추가 가능
    // @Indexed
    private double latitude;

    // @Indexed
    private double longitude;

    // Lombok @AllArgsConstructor 사용 안 할 경우:
    // public Location(String userId, double latitude, double longitude) {
    //     this.userId = userId;
    //     this.latitude = latitude;
    //     this.longitude = longitude;
    // }
}