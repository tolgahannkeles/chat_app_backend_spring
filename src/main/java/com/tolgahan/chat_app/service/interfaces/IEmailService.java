package com.tolgahan.chat_app.service.interfaces;

import com.tolgahan.chat_app.model.User;

import java.util.List;

public interface IEmailService {
    User getUserByEmail(String email);

    List<String> getEmailsByUser(User user);
}
