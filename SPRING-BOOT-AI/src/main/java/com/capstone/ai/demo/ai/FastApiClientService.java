package com.capstone.ai.demo.ai; //ai 패키지

import com.capstone.ai.demo.ai.dto.ChatResponseDto;          // FastAPI 응답 DTO
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service //스프링이 관리하는 서비스 빈
public class FastApiClientService {
    // HTTP 요청을 보내기 위한 객체 (간단하게 new 로 생성)
    private final RestTemplate restTemplate = new RestTemplate();

    // FastAPI 서버 주소 (지금은 로컬에서 8000 포트로 실행한다고 가정)
    private final String FASTAPI_URL = "http://localhost:8000/ai/chat";

    // Spring Boot에서 받은 질문 텍스트를 FastAPI에 전달하고,
    // FastAPI가 돌려준 AI 답변(content)을 문자열로 리턴하는 메서드
    public String askToAi(String userMessage) {

        // 1) FastAPI 쪽 ChatReq에 맞게 messages[0] 구조 만들기
        Map<String, Object> messageObj = new HashMap<>();
        messageObj.put("role", "user");        // role은 user로 고정
        messageObj.put("content", userMessage); // 실제 사용자 질문/설문 내용

        // 2) FastAPI /ai/chat 이 받는 전체 JSON 바디 형태
        Map<String, Object> body = new HashMap<>();
        body.put("messages", new Object[]{ messageObj }); // messages 배열
        body.put("stream", false);                        // stream 옵션 (지금은 false)
        body.put("temperature", 0.2);                     // 온도값 (고정)

        // 3) HTTP 헤더 설정 (JSON 형식으로 보낸다고 명시)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 4) 헤더 + 바디를 하나의 요청 엔티티로 포장
        HttpEntity<Map<String, Object>> req =
                new HttpEntity<>(body, headers);

        // 5) FastAPI 서버에 POST 요청 보내고 응답을 ChatResponseDto로 받기
        ResponseEntity<ChatResponseDto> resp =
                restTemplate.postForEntity(FASTAPI_URL, req, ChatResponseDto.class);

        // 6) 응답 JSON의 content 필드만 꺼내서 반환
        return resp.getBody().getContent();
    }
}
