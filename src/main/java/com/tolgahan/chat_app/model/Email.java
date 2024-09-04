package com.tolgahan.chat_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Email {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private boolean isVerified;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Email(String email, User user) {
        this.email = email;
        this.user = user;
        this.isVerified = false;
    }


}
