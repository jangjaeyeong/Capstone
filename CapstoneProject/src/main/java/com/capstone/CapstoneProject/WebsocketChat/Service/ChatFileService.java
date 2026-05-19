package com.capstone.CapstoneProject.WebsocketChat.Service;
import com.capstone.CapstoneProject.Community.S3Service;
import com.capstone.CapstoneProject.WebsocketChat.FileDTO.ChatFileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ChatFileService {

    private final S3Service s3Service;

    public ChatFileService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public ChatFileResponse saveFile(String file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("파일이 비어 있습니다.");
            }

             file = UUID.randomUUID().toString() + "_" + file;
            String originalName = "images/" + file;

            String fileUrl = s3Service.createPresignedUrl(originalName);
            originalName = fileUrl.split("\\?")[0];
            return new ChatFileResponse(fileUrl, originalName);
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}