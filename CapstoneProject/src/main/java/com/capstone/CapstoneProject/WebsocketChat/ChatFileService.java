package com.capstone.CapstoneProject.WebsocketChat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ChatFileService {

    private final Path uploadDir = Paths.get("uploads/chat");

    public ChatFileResponse saveFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("파일이 비어 있습니다.");
            }

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String savedName = UUID.randomUUID() + extension;
            Path savePath = uploadDir.resolve(savedName);

            file.transferTo(savePath.toFile());

            String fileUrl = "/uploads/chat/" + savedName;

            return new ChatFileResponse(fileUrl, originalName);
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}