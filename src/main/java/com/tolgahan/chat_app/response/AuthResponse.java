package com.tolgahan.chat_app.response;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthResponse {
    private UUID userId;
    private String accessToken;
    private String refreshToken;
}
