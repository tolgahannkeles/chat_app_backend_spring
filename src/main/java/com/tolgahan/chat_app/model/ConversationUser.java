package com.tolgahan.chat_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    private boolean isMuted;
    private boolean isDeleted;

    private LocalDateTime createdAt;

    public ConversationUser(User user, Conversation conversation) {
        this.user = user;
        this.conversation = conversation;
        this.isDeleted=false;
        this.isMuted=false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
