package com.example.demo.entity.Post;

import javax.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment; // 댓글 내용

    @Column(name = "created_date")
    @CreatedDate
    private String createdDate;

    @Column()
    private Long userId;

    @Column()
    private Long postId;
}