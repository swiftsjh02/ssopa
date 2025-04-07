package com.example.demo.dto.Comment;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommentRequestDto {
    private Long id;
    private String comment;
}