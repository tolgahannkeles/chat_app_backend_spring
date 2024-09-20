package com.tolgahan.chat_app.service.interfaces;

import com.tolgahan.chat_app.enums.FriendshipStatus;
import com.tolgahan.chat_app.model.Friendship;
import com.tolgahan.chat_app.model.User;

import java.util.UUID;

public interface IFriendshipService {
    FriendshipStatus getFriendshipStatus(UUID senderId, UUID receiverId);

    Friendship getFriendship(UUID senderId, UUID receiverId);

    Friendship getFriendshipById(Long id);

    void saveFriendship(Friendship friendship);

    void deleteFriendship(Friendship friendship);

    void sendFriendRequest(User sender, UUID receiverId);

    Friendship updateFriendship(User local, UUID senderId, FriendshipStatus newStatus);
}
