package com.tolgahan.chat_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.EmailRepository;
import com.tolgahan.chat_app.repository.UserRepository;
import com.tolgahan.chat_app.utils.ResponseCreator;
import com.tolgahan.chat_app.validation.RegistrationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(ObjectMapper objectMapper, UserRepository userRepository, EmailRepository emailRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }




    @GetMapping
    public ResponseEntity<String> getUser(@RequestParam UUID id) {

        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseCreator.notFound();
            }

            String json = objectMapper.writeValueAsString(user);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(json);

        } catch (Exception e) {
            logger.error("Error getting user: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error getting user: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<String> all() {
        try {
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(userRepository.findAll()));
        } catch (Exception e) {
            return ResponseCreator.internalServerError("Error: " + e.getMessage());
        }

    }


}
