package com.capstone.CapstoneProject.WebsocketChat;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByProjectIdOrderByCreatedAtAsc(Long projectId);

    Optional<ChatMessage> findTopByProjectIdOrderByIdDesc(Long projectId);

    long countByProjectIdAndIdGreaterThanAndSenderNicknameNot(
            Long projectId,
            Long lastReadMessageId,
            String senderNickname
    );

    List<ChatMessage> findByProjectIdAndMessageTypeInOrderByCreatedAtDesc(
            Long projectId,
            Collection<ChatMessageType> messageTypes
    );
}