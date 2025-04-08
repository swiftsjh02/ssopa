package com.example.demo.dto.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDto {
    private String title;


    private String category;

    private String content;


}