package com.capstone.CapstoneProject;

import com.capstone.CapstoneProject.AiCaller.AiService;
import com.capstone.CapstoneProject.AiCaller.ChatRequestDTO;
import com.capstone.CapstoneProject.AiCaller.QuizRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final AiService aiService;

    @PostMapping("/api/error")
    String error() {
        return "ExceptionHandler";
    }

    @PostMapping("/api/quiz")
    public ResponseEntity<ResponseEntity<Map>> aiCalling
            (@RequestBody QuizRequestDto reqDto) {
        System.out.println(reqDto.getAnswers());
        ResponseEntity<Map> responseAI = aiService.callingAI(reqDto);
        return ResponseEntity.ok(responseAI);
    }
    @PostMapping("/api/chat")
    Map<String, Collection> aiChat(@RequestBody ChatRequestDTO chatReqDto) {
        if(chatReqDto.getNickname() == null || chatReqDto.getNickname().equals("")) {
            chatReqDto.setNickname("사용자");
        }
        Map<String, Collection> chat = aiService.chatService(chatReqDto);
        System.out.println("최종 ai 답변 : " + chat);
        return chat;
    }
}
