package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.controller.UserController;
import com.tolgahan.chat_app.enums.FriendshipStatus;
import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.Friendship;
import com.tolgahan.chat_app.model.Role;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.EmailRepository;
import com.tolgahan.chat_app.repository.RoleRepository;
import com.tolgahan.chat_app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailRepository emailService;
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final String DEFAULT_ROLE = "ADMIN";
    private final FriendshipService friendshipService;

    public UserService(UserRepository userRepository, EmailRepository emailService, RoleRepository roleRepository, FriendshipService friendshipService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.friendshipService = friendshipService;
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElse(null);
    }

    public User getUserById(UUID id) {
        return userRepository.findUserById(id).orElse(null);
    }

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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void sendFriendRequest(User sender, User receiver) {
        if (friendshipService.getFriendshipStatus(sender.getId(), receiver.getId()) == null) {
            sender.getSentFriendRequests().add(new Friendship(sender, receiver, FriendshipStatus.PENDING));
            userRepository.save(sender);
        } else {
            logger.error("Friendship already exists");
            throw new RuntimeException("Friendship already exists");
        }
    }

    public void acceptFriendRequest(Friendship friendship) {
        if (friendship.getStatus() == FriendshipStatus.PENDING) {
            friendship.setStatus(FriendshipStatus.ACCEPTED);
            friendshipService.saveFriendship(friendship);
        } else {
            logger.error("No pending friendship found");
            throw new RuntimeException("No pending friendship found");
        }

    }

    public void rejectFriendRequest(Friendship friendship) {
        if (friendship.getStatus() == FriendshipStatus.PENDING) {
            friendshipService.deleteFriendship(friendship);
        } else {
            logger.error("No pending friendship found");
            throw new RuntimeException("No pending friendship found");
        }

    }
}
