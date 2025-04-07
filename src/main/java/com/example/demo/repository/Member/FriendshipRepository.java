package com.example.demo.repository.Member;

import com.example.demo.entity.Member.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {


    List<Friendship> findByAddresseeEmailAndStatus(String email, Friendship.Status status);
}
