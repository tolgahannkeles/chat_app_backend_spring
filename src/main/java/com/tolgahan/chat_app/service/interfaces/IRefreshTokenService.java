package com.tolgahan.chat_app.service.interfaces;

import com.tolgahan.chat_app.model.RefreshToken;
import com.tolgahan.chat_app.model.User;

import java.util.UUID;

public interface IRefreshTokenService {
    String generateRefreshToken(User user);

    RefreshToken getByUserId(UUID userId);

    boolean isRefreshTokenExpired(RefreshToken token);

    void deleteRefreshToken(RefreshToken token);
}
