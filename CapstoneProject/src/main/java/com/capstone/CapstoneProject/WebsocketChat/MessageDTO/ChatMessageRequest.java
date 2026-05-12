package com.capstone.CapstoneProject.WebsocketChat.MessageDTO;


import lombok.Data;

import java.util.List;

@Data
public class ChatMessageRequest {

    private Long projectId;
    private String senderNickname;
    private String content;
    private String messageType;
    private String fileUrl;
    private String originalFileName;
    private List<String> mentionedNicknames;
}
