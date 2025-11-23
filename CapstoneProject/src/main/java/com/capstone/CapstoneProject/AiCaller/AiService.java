package com.capstone.CapstoneProject.AiCaller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AiService {
    private final RestTemplate restTemplate;


     public  ResponseEntity<Map> callingAI(requestDto aiDto) {
        String pythonUrl = "http://localhost:8000/ai/chat";
         StringBuilder responseBuilder = new StringBuilder();
         responseBuilder.append("조건: ");
         for (AnswerDto answerDto : aiDto.getAnswers()) {
            responseBuilder.append("Q: ").append(answerDto.getQuestion()).append(" ");
            responseBuilder.append("A: ").append(answerDto.getValue()).append("\n");
         }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> aiResponse = new HashMap<>();
        aiResponse.put("content", responseBuilder.toString());
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(aiResponse);

        Map<String, Object> body = new HashMap<>();
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        System.out.println(body);
        ResponseEntity<Map> res = restTemplate.postForEntity(pythonUrl, entity, Map.class);
         System.out.println("AI 답변: " + res.getBody());
         System.out.println("res 전체: " + res);
        return res;
    }
}