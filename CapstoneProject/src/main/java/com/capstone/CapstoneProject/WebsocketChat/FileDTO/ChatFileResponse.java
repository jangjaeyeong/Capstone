package com.capstone.CapstoneProject.WebsocketChat.FileDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatFileResponse {

    private String filedUrl;
    private String originalFileName;
    private String fileName;
    private String fileType;
    private String disposition;
}