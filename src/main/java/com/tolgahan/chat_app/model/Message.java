package com.tolgahan.chat_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Message {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    private String message;
    private Boolean isDeleted;
    private Boolean isRead;
    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "deletion_type_id")
    private DeletionType deletionType;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }
}
