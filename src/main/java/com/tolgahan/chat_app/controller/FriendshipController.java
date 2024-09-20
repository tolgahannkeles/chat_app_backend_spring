package com.tolgahan.chat_app.controller;

import com.tolgahan.chat_app.controller.interfaces.IFriendshipController;
import com.tolgahan.chat_app.enums.FriendshipStatus;
import com.tolgahan.chat_app.exceptions.BadRequestException;
import com.tolgahan.chat_app.exceptions.TokenIsNotValidException;
import com.tolgahan.chat_app.model.Email;
import com.tolgahan.chat_app.model.Friendship;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.request.friendship.FriendshipCreateRequest;
import com.tolgahan.chat_app.request.friendship.FriendshipUpdateRequest;
import com.tolgahan.chat_app.response.FriendResponse;
import com.tolgahan.chat_app.response.FriendshipResponse;
import com.tolgahan.chat_app.service.interfaces.IFriendshipService;
import com.tolgahan.chat_app.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FriendshipController implements IFriendshipController {

    private final IUserService userService;
    private final Logger logger = LoggerFactory.getLogger(FriendshipController.class);
    private final IFriendshipService friendshipService;

    public FriendshipController(IUserService userService, IFriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userService.getUserByUsername(userDetails.getUsername());
        }
        return null;
    }


    @Override
    public List<FriendResponse> getAllFriends() {
        try {
            logger.info("Getting friends");
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }

            List<User> friends = user.getFriends();
            return friends.stream().map(friend -> {
                FriendResponse response = new FriendResponse();
                response.setId(friend.getId());
                response.setUsername(friend.getUsername());
                response.setEmail(friend.getEmails().stream().map(Email::getEmail).toList());
                response.setFriendshipStatus(friendshipService.getFriendshipStatus(user.getId(), friend.getId()));
                return response;
            }).toList();

        } catch (Exception e) {
            logger.error("Error getting friends: {}", e.getMessage());
            throw new BadRequestException("Error getting friends: " + e.getMessage());
        }
    }

    @Override
    public FriendshipResponse getStatusWithFriend(UUID friendId) {
        try {
            logger.info("Getting friendship status with friend {}", friendId);
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }
            Friendship friendship;

            friendship = friendshipService.getFriendship(user.getId(), friendId);

            if (friendship == null) {
                friendship = friendshipService.getFriendship(friendId, user.getId());

                if (friendship == null) {
                    return null;
                }

            }


            return new FriendshipResponse(friendship);
        } catch (Exception e) {
            logger.error("Error getting friendship status with friend: {}", e.getMessage());
            throw new BadRequestException("Error getting friendship status with friend: " + e.getMessage());
        }
    }

    @Override
    public List<FriendshipResponse> getAllFriendRequests() {
        try {
            logger.info("Getting received friend requests");
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }

            List<Friendship> friendships = user.getReceivedFriendRequests();
            List<FriendshipResponse> responses = new ArrayList<>();

            friendships.forEach(friendship -> {
                if (friendship.getStatus() == FriendshipStatus.PENDING) {
                    responses.add(new FriendshipResponse(friendship));
                }
            });

            return responses;

        } catch (Exception e) {
            logger.error("Error getting all friend requests: {}", e.getMessage());
            throw new BadRequestException("Error getting all friend requests: " + e.getMessage());
        }
    }

    @Override
    public Boolean sendFriendRequest(FriendshipCreateRequest request) {
        try {
            logger.info("Sending friend request");
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }
            friendshipService.sendFriendRequest(user, request.getUserId());
            return true;

        } catch (Exception e) {
            logger.error("Error sending friend request: {}", e.getMessage());
            throw new BadRequestException("Error sending friend request: " + e.getMessage());
        }
    }

    @Override
    public FriendshipResponse updateFriendshipRequestStatus(UUID friendId, FriendshipUpdateRequest request) {
        try {
            logger.info("Changing friendship status");
            User user = getCurrentUser();
            if (user == null) {
                logger.error("User not found.");
                throw new TokenIsNotValidException();
            }

            Friendship updateFriendship = friendshipService.updateFriendship(user, friendId, request.getStatus());
            return new FriendshipResponse(updateFriendship);
        } catch (Exception e) {
            logger.error("Error changing friendship status: {}", e.getMessage());
            throw new BadRequestException("Error changing friendship status: " + e.getMessage());
        }
    }
}
