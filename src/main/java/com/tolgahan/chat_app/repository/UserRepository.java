package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserById(UUID id);
}
