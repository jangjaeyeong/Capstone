package com.capstone.CapstoneProject.WebsocketChat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Long> {

    Optional<ChatReadStatus> findByProjectIdAndNickname(Long projectId, String nickname);

    List<ChatReadStatus> findByNickname(String nickname);

    long countByProjectIdAndLastReadMessageIdGreaterThanEqual(
            Long projectId,
            Long messageId
    );
}