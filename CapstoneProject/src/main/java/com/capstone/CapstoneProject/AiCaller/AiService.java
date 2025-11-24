package com.capstone.CapstoneProject.AiCaller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AiService {
    private final RestTemplate restTemplate;
    private final String pythonUrl = "http://localhost:8000/ai/chat";
    private final StringBuilder responseBuilder = new StringBuilder();
    private HttpHeaders headers = new HttpHeaders();
    private Map<String, String> aiResponse = new HashMap<>();
    private List<Map<String, String>> messages = new ArrayList<>();
    private Map<String, Object> body = new HashMap<>();

    public ResponseEntity<Map> callingAI(QuizRequestDto aiDto) {

        responseBuilder.append("조건: ");
        for (QuizAnswerDto answerDto : aiDto.getAnswers()) {
            responseBuilder.append("Q: ").append(answerDto.getQuestion()).append(" ");
            responseBuilder.append("A: ").append(answerDto.getValue()).append("\n");
        }

        headers.setContentType(MediaType.APPLICATION_JSON);
        aiResponse.put("content", responseBuilder.toString());
        messages.add(aiResponse);

        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        System.out.println("사용자 답변: " + body);
        ResponseEntity<Map> res = restTemplate.postForEntity(pythonUrl, entity, Map.class);
        System.out.println("AI 답변: " + res.getBody());
        return res;
    }

    public Map<String, Collection> chatService(ChatRequestDTO chatReqDto) {
        headers.setContentType(MediaType.APPLICATION_JSON);

        responseBuilder.append("조건: ");
        for(QuizAnswerDto dto : chatReqDto.getQuizAnswers()) {
            responseBuilder.append("Q: ").append(dto.getQuestion()).append(" ");
            responseBuilder.append("A: ").append(dto.getValue()).append("\n");
        }
        for(ChatMessageDTO dto : chatReqDto.getMessages()) {
            if(dto.getRole().equals("user")) {
                responseBuilder.append(dto.getContent());
            }
        }
        aiResponse.put("content", responseBuilder.toString());
        messages.add(aiResponse);
        body.put("messages", messages);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map>  res = restTemplate.postForEntity(pythonUrl, entity, Map.class);

        System.out.println("사용자 조건: "+ body);
        System.out.println("AI Chat 답변: " + res.getBody());
        Map<String, Collection> response = new HashMap<>();
        response.put("reply", res.getBody().values());
        return response;
    }
}