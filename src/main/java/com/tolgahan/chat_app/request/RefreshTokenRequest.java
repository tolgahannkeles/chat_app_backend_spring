package com.tolgahan.chat_app.request;

import lombok.Data;

import java.util.UUID;

@Data
public class RefreshTokenRequest {
    private UUID userId;
    private String refreshToken;
}
