package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {

    Optional<User> findUserByEmail(String email);
}
