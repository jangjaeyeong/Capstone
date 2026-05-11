package com.capstone.CapstoneProject.WebsocketChat;

import lombok.Getter;

@Getter
public class ChatFileResponse {

    private String fileUrl;
    private String originalFileName;

    public ChatFileResponse(String fileUrl, String originalFileName) {
        this.fileUrl = fileUrl;
        this.originalFileName = originalFileName;
    }
}