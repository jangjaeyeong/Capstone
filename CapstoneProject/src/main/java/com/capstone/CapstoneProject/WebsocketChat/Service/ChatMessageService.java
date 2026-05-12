package com.capstone.CapstoneProject.WebsocketChat.Service;

import com.capstone.CapstoneProject.WebsocketChat.*;
import com.capstone.CapstoneProject.WebsocketChat.Entity.ChatMention;
import com.capstone.CapstoneProject.WebsocketChat.Entity.ChatMessage;
import com.capstone.CapstoneProject.WebsocketChat.FileDTO.ChatFileItemResponse;
import com.capstone.CapstoneProject.WebsocketChat.MessageDTO.ChatMessageRequest;
import com.capstone.CapstoneProject.WebsocketChat.MessageDTO.ChatMessageResponse;
import com.capstone.CapstoneProject.WebsocketChat.MessageDTO.ChatRoomSummaryResponse;
import com.capstone.CapstoneProject.WebsocketChat.Repository.ChatMentionRepository;
import com.capstone.CapstoneProject.WebsocketChat.Repository.ChatMessageRepository;
import com.capstone.CapstoneProject.WebsocketChat.Repository.ChatReadStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMentionRepository chatMentionRepository;

    public ChatMessageService(
            ChatMessageRepository chatMessageRepository,
            ChatReadStatusRepository chatReadStatusRepository,
            ChatMentionRepository chatMentionRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatReadStatusRepository = chatReadStatusRepository;
        this.chatMentionRepository = chatMentionRepository;
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

        saveMentions(projectId, saved.getId(), request.getMentionedNicknames());

        return toResponse(saved, request.getSenderNickname(), 0);
    }

    public List<ChatMessageResponse> getMessages(
            Long projectId,
            String nickname,
            long participantCount
    ) {
        return chatMessageRepository.findByProjectIdOrderByCreatedAtAsc(projectId)
                .stream()
                .map(message -> toResponse(message, nickname, participantCount))
                .toList();
    }

    @Transactional
    public void markAsRead(Long projectId, String nickname) {
        Long lastMessageId = chatMessageRepository.findTopByProjectIdOrderByIdDesc(projectId)
                .map(ChatMessage::getId)
                .orElse(0L);

        ChatReadStatus status = chatReadStatusRepository
                .findByProjectIdAndNickname(projectId, nickname)
                .orElseGet(() -> {
                    ChatReadStatus created = new ChatReadStatus();
                    created.setProjectId(projectId);
                    created.setNickname(nickname);
                    created.setLastReadMessageId(0L);
                    return created;
                });

        status.setLastReadMessageId(lastMessageId);
        chatReadStatusRepository.save(status);

        chatMentionRepository
                .findByProjectIdAndMentionedNicknameAndReadFalse(projectId, nickname)
                .forEach(mention -> {
                    mention.setRead(true);
                    chatMentionRepository.save(mention);
                });
    }

    public List<ChatFileItemResponse> getFiles(Long projectId) {
        return chatMessageRepository
                .findByProjectIdAndMessageTypeInOrderByCreatedAtDesc(
                        projectId,
                        List.of(ChatMessageType.IMAGE, ChatMessageType.FILE)
                )
                .stream()
                .map(this::toFileResponse)
                .toList();
    }

    public ChatRoomSummaryResponse getRoomSummary(
            Long projectId,
            String roomName,
            String nickname
    ) {
        ChatRoomSummaryResponse response = new ChatRoomSummaryResponse();
        response.setProjectId(projectId);
        response.setRoomName(roomName);

        Long lastReadId = chatReadStatusRepository
                .findByProjectIdAndNickname(projectId, nickname)
                .map(ChatReadStatus::getLastReadMessageId)
                .orElse(0L);

        chatMessageRepository.findTopByProjectIdOrderByIdDesc(projectId)
                .ifPresent(last -> {
                    response.setLastMessage(makeLastMessageText(last));
                    response.setLastMessageType(last.getMessageType().name());
                    response.setLastMessageTime(last.getCreatedAt());
                });

        long unreadCount = chatMessageRepository
                .countByProjectIdAndIdGreaterThanAndSenderNicknameNot(
                        projectId,
                        lastReadId,
                        nickname
                );

        long mentionCount = chatMentionRepository
                .countByProjectIdAndMentionedNicknameAndReadFalse(projectId, nickname);

        response.setUnreadCount(unreadCount);
        response.setMentionCount(mentionCount);

        return response;
    }

    private void saveMentions(
            Long projectId,
            Long messageId,
            List<String> mentionedNicknames
    ) {
        if (mentionedNicknames == null || mentionedNicknames.isEmpty()) {
            return;
        }

        for (String nickname : mentionedNicknames) {
            if (nickname == null || nickname.isBlank()) {
                continue;
            }

            ChatMention mention = new ChatMention();
            mention.setProjectId(projectId);
            mention.setMessageId(messageId);
            mention.setMentionedNickname(nickname.trim());

            chatMentionRepository.save(mention);
        }
    }

    private ChatMessageResponse toResponse(
            ChatMessage message,
            String currentNickname,
            long participantCount
    ) {
        ChatMessageResponse response = new ChatMessageResponse();

        response.setId(message.getId());
        response.setProjectId(message.getProjectId());
        response.setSenderNickname(message.getSenderNickname());
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType().name());
        response.setFileUrl(message.getFileUrl());
        response.setOriginalFileName(message.getOriginalFileName());
        response.setCreatedAt(message.getCreatedAt());

        long readCount = chatReadStatusRepository
                .countByProjectIdAndLastReadMessageIdGreaterThanEqual(
                        message.getProjectId(),
                        message.getId()
                );

        response.setReadCount(readCount);

        if (participantCount > 0) {
            long unreadCount = Math.max(0, participantCount - readCount);
            response.setUnreadCount(unreadCount);
        } else {
            response.setUnreadCount(0);
        }

        boolean mentionedMe = false;

        if (currentNickname != null && !currentNickname.isBlank()) {
            mentionedMe = chatMentionRepository
                    .countByProjectIdAndMentionedNicknameAndReadFalse(
                            message.getProjectId(),
                            currentNickname
                    ) > 0;
        }

        response.setMentionedMe(mentionedMe);

        return response;
    }

    private ChatFileItemResponse toFileResponse(ChatMessage message) {
        ChatFileItemResponse response = new ChatFileItemResponse();

        response.setMessageId(message.getId());
        response.setProjectId(message.getProjectId());
        response.setUploaderNickname(message.getSenderNickname());
        response.setFileUrl(message.getFileUrl());
        response.setOriginalFileName(message.getOriginalFileName());
        response.setFileType(message.getMessageType().name());
        response.setCreatedAt(message.getCreatedAt());

        return response;
    }

    private String makeLastMessageText(ChatMessage message) {
        if (message.getMessageType() == ChatMessageType.IMAGE) {
            return "사진을 보냈습니다.";
        }

        if (message.getMessageType() == ChatMessageType.FILE) {
            return "파일을 보냈습니다.";
        }

        if (message.getMessageType() == ChatMessageType.SYSTEM) {
            return message.getContent();
        }

        return message.getContent();
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
}