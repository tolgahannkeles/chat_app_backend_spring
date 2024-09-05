package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.Friendship;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.FriendshipStatusRequest;
import com.tolgahan.chat_app.response.FriendResponse;
import com.tolgahan.chat_app.security.JwtTokenProvider;
import com.tolgahan.chat_app.service.FriendshipService;
import com.tolgahan.chat_app.service.UserService;
import com.tolgahan.chat_app.utils.ResponseCreator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FriendshipService friendshipService;

    public AccountController(UserService userService, JwtTokenProvider jwtTokenProvider, FriendshipService friendshipService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.friendshipService = friendshipService;
    }


    @GetMapping
    public ResponseEntity<String> getUser(HttpServletRequest request) {
        try {
            logger.info("Getting account info");
            User user = getUserIdFromRequest(request);
            if (user == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }
            System.out.println(user);
            return ResponseCreator.ok(user);

        } catch (Exception e) {
            logger.error("Error getting user: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error getting user: " + e.getMessage());
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<String> getFriends(HttpServletRequest request) {
        try {
            logger.info("Getting friends");
            User user = getUserIdFromRequest(request);
            if (user == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }

            List<FriendResponse> responses = user.getFriends().stream().map(friend -> {
                FriendResponse response = new FriendResponse();
                response.setId(friend.getId());
                response.setUsername(friend.getUsername());
                response.setEmails(friend.getEmails().stream().map(Email::getEmail).toList());
                return response;
            }).toList();
            return ResponseCreator.ok(responses);
        } catch (Exception e) {
            logger.error("Error getting friends: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error getting friends: " + e.getMessage());
        }
    }

    @PostMapping("/friends/add")
    public ResponseEntity<String> addFriend(HttpServletRequest request, @RequestBody Map<String, Object> requestBody) {
        try {
            logger.info("Adding friend");
            User user = getUserIdFromRequest(request);
            if (user == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }
            User friend = userService.getUserById(UUID.fromString(requestBody.get("id").toString()));
            if (friend == null) {
                logger.error("Friend not found.");
                return ResponseCreator.notFound();
            }
            userService.sendFriendRequest(user, friend);
            return ResponseCreator.ok("Friend added successfully");
        } catch (Exception e) {
            logger.error("Error adding friend: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error adding friend: " + e.getMessage());
        }
    }


    @GetMapping("/friends/received")
    public ResponseEntity<String> getReceivedFriendRequests(HttpServletRequest request) {
        try {
            logger.info("Getting received friend requests");
            User user = getUserIdFromRequest(request);
            if (user == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }
            List<FriendResponse> responses = user.getReceivedFriendRequests().stream().map(friendship -> {
                FriendResponse response = new FriendResponse();
                response.setId(friendship.getSender().getId());
                response.setUsername(friendship.getSender().getUsername());
                response.setEmails(friendship.getSender().getEmails().stream().map(Email::getEmail).toList());
                return response;
            }).toList();
            return ResponseCreator.ok(responses);
        } catch (Exception e) {
            logger.error("Error getting received friend requests: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error getting received friend requests: " + e.getMessage());
        }
    }

    @GetMapping("/friends/sent")
    public ResponseEntity<String> getSentFriendRequests(HttpServletRequest request) {
        try {
            logger.info("Getting received friend requests");
            User user = getUserIdFromRequest(request);
            if (user == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }
            List<FriendResponse> responses = user.getSentFriendRequests().stream().map(friendship -> {
                FriendResponse response = new FriendResponse();
                response.setId(friendship.getSender().getId());
                response.setUsername(friendship.getSender().getUsername());
                response.setEmails(friendship.getSender().getEmails().stream().map(Email::getEmail).toList());
                return response;
            }).toList();
            return ResponseCreator.ok(responses);
        } catch (Exception e) {
            logger.error("Error getting received friend requests: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error getting received friend requests: " + e.getMessage());
        }
    }

    @PostMapping("/friends/accept")
    public ResponseEntity<String> acceptFriendRequest(HttpServletRequest request, @RequestBody FriendshipStatusRequest friendshipStatusRequest) {
        try {
            logger.info("Accepting friend request");
            User receiver = getUserIdFromRequest(request);
            if (receiver == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }

            if (!receiver.getId().equals(friendshipStatusRequest.getReceiverId())) {
                logger.error("Receiver Id is wrong.");
                return ResponseCreator.badRequest(("Receiver Id is wrong." + friendshipStatusRequest.getReceiverId()) + " " + receiver.getId());
            }

            User sender = userService.getUserById(friendshipStatusRequest.getSenderId());
            if (sender == null) {
                logger.error("Sender not found.");
                return ResponseCreator.notFound();
            }
            Friendship friendship = friendshipService.getFriendship(sender.getId(), receiver.getId());
            if (friendship == null) {
                logger.error("No request from id: {}", friendshipStatusRequest.getSenderId());
                return ResponseCreator.badRequest("No request from id: " + friendshipStatusRequest.getSenderId());
            }
            userService.acceptFriendRequest(friendship);
            return ResponseCreator.ok("Friend request accepted successfully");
        } catch (Exception e) {
            logger.error("Error accepting friend request: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error accepting friend request: " + e.getMessage());
        }
    }

    @PostMapping("/friends/reject")
    public ResponseEntity<String> rejectFriendRequest(HttpServletRequest request, @RequestBody FriendshipStatusRequest friendshipStatusRequest) {
        try {
            logger.info("Accepting friend request");
            User receiver = getUserIdFromRequest(request);
            if (receiver == null) {
                logger.error("User not found.");
                return ResponseCreator.notFound();
            }

            if (!receiver.getId().equals(friendshipStatusRequest.getReceiverId())) {
                logger.error("Receiver Id is wrong.");
                return ResponseCreator.badRequest(("Receiver Id is wrong." + friendshipStatusRequest.getReceiverId()) + " " + receiver.getId());
            }

            User sender = userService.getUserById(friendshipStatusRequest.getSenderId());
            if (sender == null) {
                logger.error("Sender not found.");
                return ResponseCreator.notFound();
            }
            Friendship friendship = friendshipService.getFriendship(sender.getId(), receiver.getId());
            if (friendship == null) {
                logger.error("No request from id: {}", friendshipStatusRequest.getSenderId());
                return ResponseCreator.badRequest("No request from id: " + friendshipStatusRequest.getSenderId());
            }
            userService.acceptFriendRequest(friendship);
            return ResponseCreator.ok("Friend request accepted successfully");
        } catch (Exception e) {
            logger.error("Error accepting friend request: {}", e.getMessage());
            return ResponseCreator.internalServerError("Error accepting friend request: " + e.getMessage());
        }
    }


    private User getUserIdFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            logger.error("Invalid or missing Authorization header");
            throw new RuntimeException("Invalid or missing Authorization header");
        }
        String token = bearerToken.substring(7);
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        logger.info("Getting user with id: {}", userId);
        return userService.getUserById(userId);
    }
}
