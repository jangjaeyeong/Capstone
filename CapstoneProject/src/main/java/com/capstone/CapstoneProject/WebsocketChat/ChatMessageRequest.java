package com.capstone.CapstoneProject.WebsocketChat;

import lombok.Data;

@Data
public class ChatMessageRequest {

    private Long projectId;
    private String senderNickname;
    private String content;
    private String messageType;
    private String fileUrl;
    private String originalFileName;
}