package com.example.demo.repository.Member;

import com.example.demo.entity.Member.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.requester.email = :myEmail OR f.addresseeEmail = :myEmail) AND f.status = 'ACCEPTED'")
    List<Friendship> findAllAcceptedFriendshipsByEmail(@Param("myEmail") String myEmail);

    List<Friendship> findByAddresseeEmailAndStatus(String email, Friendship.Status status);
}
