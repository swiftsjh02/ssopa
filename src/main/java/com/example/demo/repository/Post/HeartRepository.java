package com.example.demo.repository.Post;

import com.example.demo.entity.Post.Heart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
    boolean existsHeartByPostIdAndUserId(Long postId, Long userId);
    Heart findByPostIdAndUserId(Long postId, Long userId);
}
