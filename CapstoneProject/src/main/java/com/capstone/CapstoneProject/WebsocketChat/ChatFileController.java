package com.capstone.CapstoneProject.WebsocketChat;

import com.capstone.CapstoneProject.Community.S3Service;
import com.capstone.CapstoneProject.WebsocketChat.FileDTO.ChatFileItemResponse;
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
    private final S3Service s3Service;
    private final S3Client s3Client;

    public ChatFileController(
            ChatFileService chatFileService,
            ChatMessageService chatMessageService, S3Service s3Service, S3Client s3Client
    ) {
        this.chatFileService = chatFileService;
        this.chatMessageService = chatMessageService;
        this.s3Service = s3Service;
        this.s3Client = s3Client;
    }

    @PostMapping(value = "/api/teamproject/{projectId}/chat/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @PathVariable Long projectId,
            @RequestPart("file") MultipartFile file
    ) {
        System.out.println("===== 파일 업로드 요청 들어옴 =====");
        System.out.println("projectId = " + projectId);

        if (file == null) {
            System.out.println("file = null");
        } else {
            System.out.println("file originalName = " + file.getOriginalFilename());
            System.out.println("file name = " + file.getName());
            System.out.println("file contentType = " + file.getContentType());
            System.out.println("file size = " + file.getSize());
            System.out.println("file empty = " + file.isEmpty());
        }
        try{
            s3Client.listObjectsV2(b -> b.bucket("provi-219592954442-ap-northeast-2-an"));
            System.out.println("버킷 연결 성공");
        }catch (Exception e) {
            System.out.println("버킷 모름!!" + e.getMessage());
        }

        String fileUrl = s3Service.createPresignedUrl("images/"+file.getOriginalFilename());
        System.out.println(fileUrl);
        chatFileService.saveFile(file);
        return ResponseEntity.ok().body(fileUrl);
    }

    @GetMapping("/api/teamproject/{projectId}/chat/files")
    public List<ChatFileItemResponse> getFiles(@PathVariable Long projectId) {
        return chatMessageService.getFiles(projectId);
    }
}
