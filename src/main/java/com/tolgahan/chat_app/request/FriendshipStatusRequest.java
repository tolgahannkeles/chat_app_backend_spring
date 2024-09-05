package com.tolgahan.chat_app.request;

import lombok.Data;

import java.util.UUID;
@Data
public class FriendshipStatusRequest {
    UUID senderId;
    UUID receiverId;
}
