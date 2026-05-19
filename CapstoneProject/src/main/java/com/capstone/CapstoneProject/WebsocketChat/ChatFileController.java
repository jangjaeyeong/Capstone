package com.capstone.CapstoneProject.WebsocketChat;

import com.capstone.CapstoneProject.Community.S3Service;
import com.capstone.CapstoneProject.WebsocketChat.FileDTO.ChatFileItemResponse;
import com.capstone.CapstoneProject.WebsocketChat.FileDTO.ChatFileRequest;
import com.capstone.CapstoneProject.WebsocketChat.FileDTO.ChatFileResponse;
import com.capstone.CapstoneProject.WebsocketChat.Service.ChatFileService;
import com.capstone.CapstoneProject.WebsocketChat.Service.ChatMessageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import java.util.List;

@RestController
public class ChatFileController {

    private final ChatFileService chatFileService;
    private final ChatMessageService chatMessageService;
    private final S3Client s3Client;

    public ChatFileController(ChatFileService chatFileService,
            ChatMessageService chatMessageService, S3Client s3Client) {
        this.chatFileService = chatFileService;
        this.chatMessageService = chatMessageService;
        this.s3Client = s3Client;
    }

    @PostMapping(value = "/api/teamproject/{projectId}/chat/files")
    public ResponseEntity<ChatFileResponse> uploadFile(
            @PathVariable Long projectId,
            @RequestBody ChatFileRequest request
            ) {
        System.out.println("===== 파일 업로드 요청 들어옴 =====");
        System.out.println("projectId = " + projectId);

        try{
            s3Client.listObjectsV2(b -> b.bucket("provi-219592954442-ap-northeast-2-an"));
            System.out.println("버킷 연결 성공");
        }catch (Exception e) {
            System.out.println("버킷 연결 실패" + e.getMessage());
        }
        System.out.println("fileName :::::" + request.getFileName());
        System.out.println("fileType :::::" + request.getContentType());

        ChatFileResponse fileResponse = chatFileService.saveFile(request.getFileName(), request.getContentType());
        System.out.println("file DTO Check ------" + fileResponse);
        return ResponseEntity.ok().body(fileResponse);
    }

    @GetMapping("/api/teamproject/{projectId}/chat/files")
    public List<ChatFileItemResponse> getFiles(@PathVariable Long projectId) {
        return chatMessageService.getFiles(projectId);
    }
}