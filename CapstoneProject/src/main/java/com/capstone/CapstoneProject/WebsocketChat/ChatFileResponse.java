package com.capstone.CapstoneProject.WebsocketChat;

public class ChatFileResponse {

    private String fileUrl;
    private String originalFileName;

    public ChatFileResponse(String fileUrl, String originalFileName) {
        this.fileUrl = fileUrl;
        this.originalFileName = originalFileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }
}