package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.RefreshToken;
import com.tolgahan.chat_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(UUID userId);
}
