package com.capstone.CapstoneProject.WebsocketChat.Service;
import com.capstone.CapstoneProject.Community.S3Service;
import com.capstone.CapstoneProject.WebsocketChat.FileDTO.ChatFileResponse;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ChatFileService {

    private final S3Service s3Service;

    public ChatFileService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public ChatFileResponse saveFile(String file, String fileType) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("파일이 비어 있습니다.");
            }

             file = UUID.randomUUID() + "_" + file;
            String originalName = "images/" + file;

            ChatFileResponse fileUrl = s3Service.createPresignedUrl(originalName, fileType);
            System.out.println(fileUrl);
            return fileUrl;
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}