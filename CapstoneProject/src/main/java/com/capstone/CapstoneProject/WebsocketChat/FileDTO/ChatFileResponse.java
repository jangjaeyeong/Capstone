package com.capstone.CapstoneProject.WebsocketChat.FileDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatFileResponse {

    private String fileUrl;
    private String originalFileName;
    private String fileType;
    private String disposition;
}