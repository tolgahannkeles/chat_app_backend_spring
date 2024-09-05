package com.tolgahan.chat_app.model;

import com.tolgahan.chat_app.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    @Column(nullable = true)
    private String profilePictureUrl;
    @Column(nullable = true)
    private String name;
    @Column(nullable = true)
    private String surname;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();


    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> sentFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> receivedFriendRequests = new ArrayList<>();


    public List<User> getFriends() {
        List<User> friends = new ArrayList<>();
        friends.addAll(sentFriendRequests.stream()
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.ACCEPTED)
                .map(Friendship::getReceiver)
                .toList());
        friends.addAll(receivedFriendRequests.stream()
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.ACCEPTED)
                .map(Friendship::getSender)
                .toList());
        return friends;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Email> emails = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationUser> conversationUsers= new ArrayList<>();

    public List<Conversation> getConversations() {
        return conversationUsers.stream().map(ConversationUser::getConversation).collect(Collectors.toList());
    }

    // Getters and Setters
}
