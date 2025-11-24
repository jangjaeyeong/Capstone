package com.capstone.ai.demo.ai;

import com.capstone.ai.demo.ai.dto.ChatRequestDto;   // 요청 DTO
import com.capstone.ai.demo.ai.dto.ChatResponseDto;  // 응답 DTO
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChatController {
    private final FastApiClientService fastApiClientService;  // FastAPI 호출 서비스 의존성

    // 생성자를 통해 FastApiClientService 주입
    public ChatController(FastApiClientService fastApiClientService) {
        this.fastApiClientService = fastApiClientService;
    }

    // POST /api/ai/chat 엔드포인트
    @PostMapping("/chat")
    public ChatResponseDto chat(@RequestBody ChatRequestDto req) {

        // 1) 프론트/포스트맨에서 넘어온 message 추출
        String question = req.getMessage();

        // 2) FastAPI에 전달해서 AI 답변 받아오기
        String answer = fastApiClientService.askToAi(question);

        // 3) 받은 답변을 ChatResponseDto로 감싸서 그대로 반환
        return new ChatResponseDto(answer);
    }
}
