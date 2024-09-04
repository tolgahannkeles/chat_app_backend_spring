package com.tolgahan.chat_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class DeletionType {
    @Id
    private Long id;

    private String type;
}
