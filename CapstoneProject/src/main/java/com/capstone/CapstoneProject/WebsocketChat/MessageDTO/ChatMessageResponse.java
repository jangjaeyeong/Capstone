package com.capstone.CapstoneProject.WebsocketChat.MessageDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {

    private Long id;
    private Long projectId;
    private String senderNickname;
    private String content;
    private String messageType;
    private String fileUrl;
    private String originalFileName;
    private LocalDateTime createdAt;
    private long readCount;
    private long unreadCount;
    private boolean mentionedMe;
}