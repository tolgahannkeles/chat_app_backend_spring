package com.tolgahan.chat_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
public class FriendRequests {
    @EmbeddedId
    private FriendRequestsId id;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private FriendRequestsStatus status;

    @ManyToOne
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private User receiver;
}


@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "senderId", column = @Column(name = "sender_id")),
        @AttributeOverride(name = "receiverId", column = @Column(name = "receiver_id"))
})
 class FriendRequestsId implements Serializable {
    private UUID senderId;
    private UUID receiverId;
}