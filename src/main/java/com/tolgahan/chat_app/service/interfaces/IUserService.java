package com.tolgahan.chat_app.service.interfaces;

import com.tolgahan.chat_app.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    User getUserByUsername(String username);

    User getUserById(UUID id);

    void saveUser(User user, String email);

    List<User> getAllUsers();

    List<User> findUserStartingWith(String username);
}
