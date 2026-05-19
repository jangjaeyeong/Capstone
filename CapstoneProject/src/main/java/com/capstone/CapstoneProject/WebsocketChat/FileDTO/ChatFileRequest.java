package com.capstone.CapstoneProject.WebsocketChat.FileDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatFileRequest {
    String fileName;
    String contentType;
}
