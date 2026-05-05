package com.capstone.CapstoneProject.WebsocketChat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(
            ChatMessageService chatMessageService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/teamproject/{projectId}/chat/send")
    public void sendMessage(
            @DestinationVariable Long projectId,
            ChatMessageRequest request
    ) {
        ChatMessageResponse savedMessage = chatMessageService.saveMessage(projectId, request);

        messagingTemplate.convertAndSend(
                "/topic/teamproject/" + projectId,
                savedMessage
        );
    }

    @GetMapping("/api/teamproject/{projectId}/chat/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long projectId) {
        return chatMessageService.getMessages(projectId);
    }
}