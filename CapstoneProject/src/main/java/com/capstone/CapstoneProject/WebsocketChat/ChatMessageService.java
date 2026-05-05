package com.capstone.CapstoneProject.WebsocketChat;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessageResponse saveMessage(Long projectId, ChatMessageRequest request) {
        ChatMessage message = new ChatMessage();
        message.setProjectId(projectId);
        message.setSenderNickname(request.getSenderNickname());
        message.setContent(request.getContent());
        message.setFileUrl(request.getFileUrl());
        message.setOriginalFileName(request.getOriginalFileName());

        ChatMessageType type = parseMessageType(request.getMessageType());
        message.setMessageType(type);

        ChatMessage saved = chatMessageRepository.save(message);

        return toResponse(saved);
    }

    public List<ChatMessageResponse> getMessages(Long projectId) {
        return chatMessageRepository.findByProjectIdOrderByCreatedAtAsc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ChatMessageType parseMessageType(String value) {
        if (value == null || value.isBlank()) {
            return ChatMessageType.TEXT;
        }

        try {
            return ChatMessageType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChatMessageType.TEXT;
        }
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setProjectId(message.getProjectId());
        response.setSenderNickname(message.getSenderNickname());
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType().name());
        response.setFileUrl(message.getFileUrl());
        response.setOriginalFileName(message.getOriginalFileName());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}