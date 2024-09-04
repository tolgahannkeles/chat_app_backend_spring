package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlackListRepository  extends JpaRepository<TokenBlackList, Long> {
    boolean existsByToken(String token);
}
