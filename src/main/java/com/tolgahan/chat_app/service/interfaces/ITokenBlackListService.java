package com.tolgahan.chat_app.service.interfaces;

public interface ITokenBlackListService {
    void add(String token);

    boolean isTokenInBlacklist(String token);
}
