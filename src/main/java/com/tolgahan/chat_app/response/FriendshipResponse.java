package com.tolgahan.chat_app.response;

import com.tolgahan.chat_app.enums.FriendshipStatus;
import com.tolgahan.chat_app.model.Friendship;
import lombok.Data;

import java.util.UUID;
@Data
public class FriendshipResponse {
    Long id;
    UUID userId;
    FriendshipStatus status;
    String username;

    public FriendshipResponse(Friendship friendship) {
        this.id = friendship.getId();
        this.userId = friendship.getSender().getId();
        this.status = friendship.getStatus();
        this.username=friendship.getSender().getUsername();
    }
}
