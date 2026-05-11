package com.capstone.CapstoneProject.WebsocketChat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMentionRepository extends JpaRepository<ChatMention, Long> {

    long countByMentionedNicknameAndReadFalse(String mentionedNickname);

    long countByProjectIdAndMentionedNicknameAndReadFalse(
            Long projectId,
            String mentionedNickname
    );

    List<ChatMention> findByProjectIdAndMentionedNicknameAndReadFalse(
            Long projectId,
            String mentionedNickname
    );
}