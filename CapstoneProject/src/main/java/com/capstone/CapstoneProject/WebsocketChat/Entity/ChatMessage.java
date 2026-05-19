package com.capstone.CapstoneProject.WebsocketChat.Entity;


import com.capstone.CapstoneProject.WebsocketChat.ChatMessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long projectId;
    @Column(nullable = false, length = 100)
    private String senderNickname;
//    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1000)
    private String messageType;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String fileUrl;
    @Column(columnDefinition = "TEXT")
    private String originalFileName;
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
}
