package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.FriendRequests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestsRepository extends JpaRepository<FriendRequests, Long> {
}
