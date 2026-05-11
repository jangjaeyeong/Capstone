package com.capstone.CapstoneProject.WebsocketChat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_read_status",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"projectId", "nickname"})
        }
)
@Getter
@Setter
public class ChatReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    @Column(nullable = false, length = 100)
    private String nickname;
    private Long lastReadMessageId = 0L;
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTime() {
        this.updatedAt = LocalDateTime.now();
    }


}