package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.model.TokenBlackList;
import com.tolgahan.chat_app.repository.TokenBlackListRepository;
import com.tolgahan.chat_app.service.interfaces.ITokenBlackListService;
import org.springframework.stereotype.Service;

@Service
public class TokenBlackListService implements ITokenBlackListService {
    private final TokenBlackListRepository tokenBlackListRepository;

    public TokenBlackListService(TokenBlackListRepository tokenBlackListRepository) {
        this.tokenBlackListRepository = tokenBlackListRepository;
    }

    @Override
    public void add(String token) {
        TokenBlackList tokenBlackList = new TokenBlackList();
        tokenBlackList.setToken(token);
        tokenBlackListRepository.save(tokenBlackList);
    }

    @Override
    public boolean isTokenInBlacklist(String token) {
        return tokenBlackListRepository.existsByToken(token);
    }
}
