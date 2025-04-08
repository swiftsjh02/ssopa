package com.example.demo.dto.post;

import com.example.demo.entity.Post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDeleteDto {
    private String title;

    public static PostDeleteDto of(Post post) {
        return PostDeleteDto.builder()
                .title(post.getTitle())
                .build();
    }
}