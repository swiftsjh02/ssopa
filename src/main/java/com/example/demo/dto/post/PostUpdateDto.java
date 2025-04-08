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
public class PostUpdateDto {

    private String title;

    private String content;


    public static PostUpdateDto of(Post post) {
        return PostUpdateDto.builder()
                .content(post.getContent())
                .title(post.getTitle())
                .build();
    }
}
