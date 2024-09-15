package com.tolgahan.chat_app.model;

import com.tolgahan.chat_app.enums.DeletionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    private String message;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean isDeleted = false;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean isRead = false;
    private Date sentAt;

    @Enumerated(EnumType.STRING)
    private DeletionType deletionType;

    @PrePersist
    protected void onCreate() {
        this.sentAt = Date.from(LocalDateTime.now().toInstant(java.time.ZoneOffset.UTC));
    }
}
