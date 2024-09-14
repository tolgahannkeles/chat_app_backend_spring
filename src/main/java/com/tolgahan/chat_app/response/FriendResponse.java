package com.tolgahan.chat_app.response;

import com.tolgahan.chat_app.enums.FriendshipStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FriendResponse {
    UUID id;
    String username;
    List<String> email;
    FriendshipStatus friendshipStatus;
}
