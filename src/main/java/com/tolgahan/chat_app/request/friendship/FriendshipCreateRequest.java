package com.tolgahan.chat_app.request.friendship;

import lombok.Data;

import java.util.UUID;

@Data
public class FriendshipCreateRequest {
    UUID userId;
}
