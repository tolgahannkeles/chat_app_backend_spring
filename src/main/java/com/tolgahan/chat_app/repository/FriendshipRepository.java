package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship,Long> {
    Optional<Friendship> getFriendshipBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
}
