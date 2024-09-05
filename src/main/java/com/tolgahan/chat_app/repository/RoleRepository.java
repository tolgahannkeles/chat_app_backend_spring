package com.tolgahan.chat_app.repository;

import com.tolgahan.chat_app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findRoleByName(String name);
    boolean existsByName(String name);
}
