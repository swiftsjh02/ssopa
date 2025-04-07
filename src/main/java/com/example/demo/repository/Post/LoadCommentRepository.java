package com.example.demo.repository.Post;

import com.example.demo.dto.Comment.LoadCommentDto;
import com.example.demo.entity.Post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoadCommentRepository extends JpaRepository<Comment, Long> {

    List<LoadCommentDto> findAllByPostId(Long post_id);

    boolean existsByPostId(Long post_id);
}
