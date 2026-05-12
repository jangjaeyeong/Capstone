package com.capstone.CapstoneProject.WebsocketChat.Entity;


import com.capstone.CapstoneProject.WebsocketChat.ChatMessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long projectId;
    @Column(nullable = false, length = 100)
    private String senderNickname;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatMessageType messageType;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String fileUrl;
    private String originalFileName;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
