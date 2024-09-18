package com.tolgahan.chat_app.service;

import com.tolgahan.chat_app.enums.ConversationType;
import com.tolgahan.chat_app.model.Conversation;
import com.tolgahan.chat_app.model.ConversationUser;
import com.tolgahan.chat_app.model.User;
import com.tolgahan.chat_app.repository.ConversationRepository;
import com.tolgahan.chat_app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final Logger logger = LoggerFactory.getLogger(ConversationService.class);
    private final UserRepository userRepository;

    public ConversationService(ConversationRepository conversationRepository, UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    public Conversation createGroupConversation(User user, String title, List<UUID> participants) {

        Conversation conversation = new Conversation();
        conversation.setConversationType(ConversationType.GROUP);
        conversation.setTitle(title);
        conversation.setCreator(user);
        conversation.getConversationUsers().add(new ConversationUser(user, conversation));

        participants.forEach(id -> {
                    try {
                        User participant = userRepository.findUserById(id).orElseThrow(() -> {
                            logger.error("User not found with id -> " + id);
                            return new RuntimeException("User not found with id -> " + id);
                        });
                        if (participant != null) {
                            conversation.getConversationUsers().add(new ConversationUser(participant, conversation));
                        }
                    } catch (Exception e) {
                        logger.error("Error -> " + e.getMessage());
                    }

                }
        );


        return conversationRepository.save(conversation);
    }

    public Conversation getConversationById(UUID id) {
        return conversationRepository.findById(id).orElse(null);
    }

    public List<Conversation> getAllConversations(User user) {
        return user.getConversations();
    }

    public List<Conversation> getAllGroups(User user) {
        List<Conversation> groups = new ArrayList<>();
        user.getConversations().forEach(conversation -> {
            if (conversation.getConversationType().equals(ConversationType.GROUP)) {
                groups.add(conversation);
            }
        });
        return groups;
    }

    public List<Conversation> getAllChats(User user) {
        List<Conversation> chats = new ArrayList<>();
        user.getConversations().forEach(conversation -> {
            if (conversation.getConversationType().equals(ConversationType.CHAT)) {
                chats.add(conversation);
            }
        });
        return chats;
    }

    public Conversation getConversationById(User user, UUID conversationId) {
        List<Conversation> conversations = user.getConversations();
        for (Conversation conversation : conversations) {
            if (conversation.getId().equals(conversationId)) {
                return conversation;
            }
        }
        logger.error("Conversation not found");
        throw new RuntimeException("Conversation not found");
    }

    public void leaveGroup(User user, UUID conversationId) {
        Conversation conversation = getConversationById(user, conversationId);
        if (conversation == null) {
            logger.error("Conversation not found");
            throw new RuntimeException("Conversation not found");
        }
        if (conversation.getCreator().getId().equals(user.getId())) {
            logger.error("Creator cannot leave the group");
            throw new RuntimeException("Creator cannot leave the group");
        }
        if (conversation.getUsers().size() == 1) {
            conversationRepository.delete(conversation);
            return;
        }
        conversation.getConversationUsers().removeIf(conversationUser -> conversationUser.getUser().getId().equals(user.getId()));
        conversationRepository.save(conversation);
    }

    public void addParticipant(User user, UUID conversationId, User participant) {
        if (user == null || participant == null) {
            logger.error("User or participant not found");
            throw new RuntimeException("User or participant not found");
        }
        if (getConversationById(user, conversationId) == null) {
            logger.error("Conversation not found");
            throw new RuntimeException("Conversation not found");
        }
        if (user.getId().equals(participant.getId())) {
            logger.error("User cannot add himself/herself to the conversation");
            throw new RuntimeException("User cannot add himself/herself to the conversation");
        }
        if (participant.getConversations().stream().anyMatch(conversation -> conversation.getId().equals(conversationId))) {
            logger.error("User is already in the conversation");
            throw new RuntimeException("User is already in the conversation");
        }
        if (getConversationById(user, conversationId).getConversationType().equals(ConversationType.CHAT)) {
            logger.error("Cannot add participant to chat");
            throw new RuntimeException("Cannot add participant to chat");
        }
        if (getConversationById(user, conversationId).getCreator().getId().equals(participant.getId())) {
            logger.error("Creator cannot be added as participant");
            throw new RuntimeException("Creator cannot be added as participant");
        }
        Conversation conversation = getConversationById(user, conversationId);
        conversation.getConversationUsers().add(new ConversationUser(participant, conversation));
        conversationRepository.save(conversation);
    }
}
