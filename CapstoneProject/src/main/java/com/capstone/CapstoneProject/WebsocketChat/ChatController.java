
package com.capstone.CapstoneProject.WebsocketChat;

import com.capstone.CapstoneProject.WebsocketChat.MessageDTO.ChatMessageRequest;
import com.capstone.CapstoneProject.WebsocketChat.MessageDTO.ChatMessageResponse;
import com.capstone.CapstoneProject.WebsocketChat.MessageDTO.ChatRoomSummaryResponse;
import com.capstone.CapstoneProject.WebsocketChat.Service.ChatMessageService;
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
        System.out.println(request.getMessageType());

        if (request.getMentionedNicknames() != null) {
            for (String nickname : request.getMentionedNicknames()) {
                if (nickname == null || nickname.isBlank()) continue;

                messagingTemplate.convertAndSend(
                        "/topic/chat/mentions/" + nickname.trim(),
                        savedMessage
                );
            }
        }
    }

    @GetMapping("/api/teamproject/{projectId}/chat/messages")
    public List<ChatMessageResponse> getMessages(
            @PathVariable Long projectId,
            @RequestParam String nickname,
            @RequestParam(defaultValue = "0") long participantCount
    ) {
        return chatMessageService.getMessages(projectId, nickname, participantCount);
    }

    @PatchMapping("/api/teamproject/{projectId}/chat/read")
    public void markAsRead(
            @PathVariable Long projectId,
            @RequestParam String nickname
    ) {
        chatMessageService.markAsRead(projectId, nickname);
    }

//    @GetMapping("/api/teamproject/{projectId}/chat/files")
//    public List<ChatFileItemResponse> getFiles(@PathVariable Long projectId) {
//        return chatMessageService.getFiles(projectId);
//    }

    @GetMapping("/api/teamproject/{projectId}/chat/summary")
    public ChatRoomSummaryResponse getRoomSummary(
            @PathVariable Long projectId,
            @RequestParam String roomName,
            @RequestParam String nickname
    ) {
        return chatMessageService.getRoomSummary(projectId, roomName, nickname);
    }
}