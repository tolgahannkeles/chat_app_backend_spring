package com.tolgahan.chat_app.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FriendResponse {
    UUID id;
    String username;
    List<String> emails;
}
