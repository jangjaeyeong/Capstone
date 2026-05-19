package com.capstone.CapstoneProject.WebsocketChat.Entity;

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
public class ChatMention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private Long messageId;
    @Column(nullable = false, length = 100)
    private String mentionedNickname;
    private Boolean read = false;
    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

}
