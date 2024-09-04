package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.EmailRepository;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public User getUserByEmail(String email) {
        return emailRepository.findUserByEmail(email).orElse(null);
    }
}
