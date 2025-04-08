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
public class PostResponseDto {
    private String title;

    private Long id;

    private String category;

    private String content;

    private String writer;


    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory().toString())
                .content(post.getContent())
                .writer(post.getWriter())
                .build();
    }

}