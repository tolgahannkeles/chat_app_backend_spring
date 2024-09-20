package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.Role;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.EmailRepository;
import com.tolgahan.chat_app.repository.RoleRepository;
import com.tolgahan.chat_app.repository.UserRepository;
import com.tolgahan.chat_app.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final EmailRepository emailService;
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final String DEFAULT_ROLE = "USER";

    public UserService(UserRepository userRepository, EmailRepository emailService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElse(null);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findUserById(id).orElseThrow(() -> {
            logger.error("User not found -> " + id);
            return new RuntimeException("User not found -> " + id);
        });
    }

    @Override
    public void saveUser(User user, String email) {
        Role role = roleRepository.findRoleByName(DEFAULT_ROLE).orElseThrow(() -> {
            logger.error("Role not found -> " + DEFAULT_ROLE);
            return new RuntimeException("Role not found -> " + DEFAULT_ROLE);
        });
        user.setRoles(List.of(role));
        userRepository.save(user);

        User savedUser = userRepository.findUserByUsername(user.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Email emailEntity = new Email(email, savedUser);
        emailService.save(emailEntity);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findUserStartingWith(String username) {
        return userRepository.findAllByUsernameStartingWith(username).orElse(null);
    }


}
