package com.tolgahan.chat_app.response;

import lombok.Data;

import java.util.UUID;
@Data
public class ConversationResponse {
    private UUID id;
    private String title;
}
