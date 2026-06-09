package com.capstone.CapstoneProject.Community;


import com.capstone.CapstoneProject.WebsocketChat.FileDTO.ChatFileResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Presigner s3Presigner;

     public ChatFileResponse createPresignedUrl(String path, String fileType, String fileName) {
         String encodedFileName = URLEncoder.encode(path, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
         String disposition = "attachment; filename=\"" + encodedFileName + "\"";
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .contentType(fileType)
                .contentDisposition(disposition)
                .build();

        PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();
        String filedUrl = s3Presigner.presignPutObject(preSignRequest).url().toString();
        String originalFileName = filedUrl.split("\\?")[0];

        return new ChatFileResponse(filedUrl, originalFileName,fileName, fileType, disposition);
    }
}