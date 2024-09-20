package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.enums.FriendshipStatus;
import com.tolgahan.chat_app.model.Friendship;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.FriendshipRepository;
import com.tolgahan.chat_app.repository.UserRepository;
import com.tolgahan.chat_app.service.interfaces.IFriendshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FriendshipService implements IFriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(FriendshipService.class);


    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    @Override
    public FriendshipStatus getFriendshipStatus(UUID senderId, UUID receiverId) {
        Friendship friendship = friendshipRepository.getFriendshipBySenderIdAndReceiverId(senderId, receiverId).orElse(null);
        if (friendship == null) {
            friendship = friendshipRepository.getFriendshipBySenderIdAndReceiverId(receiverId, senderId).orElse(null);
            return friendship == null ? null : friendship.getStatus();
        }
        return friendship.getStatus();
    }

    @Override
    public Friendship getFriendship(UUID senderId, UUID receiverId) {
        return friendshipRepository.getFriendshipBySenderIdAndReceiverId(senderId, receiverId).orElse(null);
    }

    @Override
    public Friendship getFriendshipById(Long id) {
        return friendshipRepository.findById(id).orElse(null);
    }

    @Override
    public void saveFriendship(Friendship friendship) {
        friendshipRepository.save(friendship);
    }

    @Override
    public void deleteFriendship(Friendship friendship) {
        friendshipRepository.delete(friendship);
    }

    @Override
    public void sendFriendRequest(User sender, UUID receiverId) {
        if (sender == null) {
            logger.error("Sender not found");
            throw new RuntimeException("Sender not found");
        }
        if (sender.getId().equals(receiverId)) {
            logger.error("Sender and receiver can not be the same user");
            throw new RuntimeException("Sender and receiver can not be the same user");
        }
        User receiver = userRepository.findUserById(receiverId).orElseThrow(() -> {
            logger.error("Receiver not found");
            return new RuntimeException("Receiver not found");
        });
        if (getFriendshipStatus(sender.getId(), receiverId) == null) {
            sender.getSentFriendRequests().add(new Friendship(sender, receiver, FriendshipStatus.PENDING));
            userRepository.save(sender);
        } else {
            logger.error("Friendship already exists");
            throw new RuntimeException("Friendship already exists");
        }
    }

    @Override
    public Friendship updateFriendship(User local, UUID senderId, FriendshipStatus newStatus) {

        if (local == null) {
            logger.error("Local user not found");
            throw new RuntimeException("Local user not found");
        }

        Friendship friendship = getFriendship(senderId, local.getId());
        if (friendship == null) {
            logger.error("Friendship not found");
            throw new RuntimeException("Friendship not found");
        }

        return switch (newStatus) {
            case ACCEPTED -> acceptFriendRequest(local, friendship);
            case REJECTED -> rejectFriendRequest(local, friendship);
            case BLOCKED -> blockFriend(local, senderId);
            default -> {
                logger.error("Invalid status");
                throw new RuntimeException("Invalid status");
            }
        };
    }

    private Friendship acceptFriendRequest(User local, Friendship friendship) {
        if (local.getId().equals(friendship.getReceiver().getId())) {
            if (friendship.getStatus() == FriendshipStatus.PENDING) {
                friendship.setStatus(FriendshipStatus.ACCEPTED);
                saveFriendship(friendship);
                return friendship;
            } else {
                logger.error("No pending friendship found");
                throw new RuntimeException("No pending friendship found");
            }

        } else {
            logger.error("User is not receiver of this friendship");
            throw new RuntimeException("User is not receiver of this friendship");
        }
    }

    private Friendship rejectFriendRequest(User local, Friendship friendship) {
        if (local.getId().equals(friendship.getReceiver().getId())) {
            if (friendship.getStatus() == FriendshipStatus.PENDING) {
                deleteFriendship(friendship);
                return null;
            } else {
                logger.error("No pending friendship found");
                throw new RuntimeException("No pending friendship found");
            }
        } else {
            logger.error("User is not receiver of this friendship");
            throw new RuntimeException("User is not receiver of this friendship");
        }
    }

    private Friendship blockFriend(User local, UUID blockedId) {
        Friendship friendship = getFriendship(local.getId(), blockedId);
        if (friendship == null) {
            friendship = getFriendship(blockedId, local.getId());
            if (friendship == null) {
                User blocked = userRepository.findUserById(blockedId).orElseThrow(() -> {
                    logger.error("Blocked user not found");
                    return new RuntimeException("Blocked user not found");
                });
                saveFriendship(new Friendship(local, blocked, FriendshipStatus.BLOCKED));
                return getFriendship(local.getId(), blockedId);
            }

        }

        friendship.setStatus(FriendshipStatus.BLOCKED);
        saveFriendship(friendship);
        return friendship;
    }


}
