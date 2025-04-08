package com.example.demo.dto.post;

import com.example.demo.entity.Post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostReadDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime created_date;
    private LocalDateTime modified_date;
    private String writer;
    private int view_cnt;
    private int like_cnt;
    private boolean noticeYn;
    private String category;
    private boolean deleteYn;
    private boolean likeYn;

    public static PostReadDto of(Post post, boolean likeYn) {
        return PostReadDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .created_date(post.getCreated_date())
                .modified_date(post.getModifiedDate())
                .writer(post.getWriter())
                .category(post.getCategory().toString())
                .view_cnt(post.getView_cnt())
                .noticeYn(post.getNoticeYn())
                .deleteYn(post.getDeleteYn())
                .like_cnt(post.getLike_cnt())
                .likeYn(likeYn)
                .build();
    }
}