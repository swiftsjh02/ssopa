package com.example.demo.dto.Comment;

import com.example.demo.entity.Post.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDeleteDto {    private String comment;

    public static com.example.demo.dto.Comment.CommentDeleteDto of(Comment comment) {
        return CommentDeleteDto.builder()
                .comment(comment.getComment())
                .build();
    }
}