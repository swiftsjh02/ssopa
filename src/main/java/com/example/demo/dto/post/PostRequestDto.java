package com.example.demo.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDto {
    @ApiModelProperty(value="글제목", example="오늘의 공지",required = true)
    private String title;

    @ApiModelProperty(value="글카테고리", example="게시판 이름(ex 뜨밤)",required = true)
    private String category;

    @ApiModelProperty(value="글내용", example="내 용",required = true)
    private String content;


}