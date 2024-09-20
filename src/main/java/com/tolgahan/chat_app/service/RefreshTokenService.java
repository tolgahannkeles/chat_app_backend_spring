package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.model.RefreshToken;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.RefreshTokenRepository;
import com.tolgahan.chat_app.service.interfaces.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService implements IRefreshTokenService {

    @Value("${chat_app.refreshToken.expires_in_seconds}")
    private Long REFRESH_TOKEN_EXPIRES_IN;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /*
    private boolean isRefreshTokenExists(RefreshToken token) {
        return token.getExpiryDate().before(new Date());
    }
     */
    @Override
    public String generateRefreshToken(User user) {
        RefreshToken token = refreshTokenRepository.findByUserId(user.getId()).orElse(null);
        if(token == null) {
            token =	new RefreshToken();
            token.setUser(user);
        }
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Date.from(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRES_IN)));
        refreshTokenRepository.save(token);
        return token.getToken();
    }
    @Override
    public RefreshToken getByUserId(UUID userId) {
        return refreshTokenRepository.findByUserId(userId).orElse(null);
    }
    @Override
    public boolean isRefreshTokenExpired(RefreshToken token) {
        return token.getExpiryDate().before(new Date());
    }
    @Override
    public void deleteRefreshToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}
