package com.example.demo.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@Setter
public class Hot {
    public Hot() {
    }
    @Builder
    public Hot(Long id, Long postId, int weight, Long userId){
        this.id = id;
        this.postId = postId;
        this.weight = weight;
        this.userId = userId;
    }
    @Id
    @GeneratedValue
    @Column()
    private Long id;
    @Column(name = "post_id", nullable = false)
    private Long postId;
    @Column(name = "weight", nullable = false)
    private int weight;
    @Column(name = "user_id", nullable = false)
    private Long userId;
}
