package com.tolgahan.chat_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ConversationType {
    @Id
    private Long id;
    private String type;
}
