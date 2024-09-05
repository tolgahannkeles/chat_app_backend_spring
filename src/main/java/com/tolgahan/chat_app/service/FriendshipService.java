package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.enums.FriendshipStatus;
import com.tolgahan.chat_app.model.Friendship;
import com.tolgahan.chat_app.repository.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public FriendshipStatus getFriendshipStatus(UUID senderId, UUID receiverId) {
        Friendship friendship = friendshipRepository.getFriendshipBySenderIdAndReceiverId(senderId, receiverId).orElse(null);
        if (friendship != null) {
            return friendship.getStatus();
        }
        return null;
    }

    public Friendship getFriendship(UUID senderId, UUID receiverId) {
        return friendshipRepository.getFriendshipBySenderIdAndReceiverId(senderId, receiverId).orElse(null);
    }

    public void saveFriendship(Friendship friendship) {
        friendshipRepository.save(friendship);
    }

    public void deleteFriendship(Friendship friendship) {
        friendshipRepository.delete(friendship);
    }

}
