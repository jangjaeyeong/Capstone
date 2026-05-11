package com.capstone.CapstoneProject.WebsocketChat;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ChatFileController {

    private final ChatFileService chatFileService;

    public ChatFileController(ChatFileService chatFileService) {
        this.chatFileService = chatFileService;
    }

    @PostMapping("/api/teamproject/{projectId}/chat/files")
    public ChatFileResponse uploadFile(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file
    ) {
        return chatFileService.saveFile(file);
    }
}
