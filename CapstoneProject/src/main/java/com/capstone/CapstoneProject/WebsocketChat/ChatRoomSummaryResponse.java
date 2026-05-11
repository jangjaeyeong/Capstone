package com.capstone.CapstoneProject.WebsocketChat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomSummaryResponse {

    private Long projectId;
    private String roomName;
    private String lastMessage;
    private String lastMessageType;
    private LocalDateTime lastMessageTime;
    private long unreadCount;
    private long mentionCount;
}
