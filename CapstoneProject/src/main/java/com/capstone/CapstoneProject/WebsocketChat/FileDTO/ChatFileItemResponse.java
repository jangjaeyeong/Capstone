package com.capstone.CapstoneProject.WebsocketChat.FileDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatFileItemResponse {

    private Long messageId;
    private Long projectId;
    private String uploaderNickname;
    private String fileUrl;
    private String originalFileName;
    private String fileType;
    private LocalDateTime createdAt;

}