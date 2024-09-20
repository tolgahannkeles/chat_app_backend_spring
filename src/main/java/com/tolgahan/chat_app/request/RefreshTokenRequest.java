package com.tolgahan.chat_app.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
public class RefreshTokenRequest {
    @NotEmpty(message = "User userId is required")
    private UUID userId;
    @NotEmpty(message = "Refresh token is required")
    private String refreshToken;
}
