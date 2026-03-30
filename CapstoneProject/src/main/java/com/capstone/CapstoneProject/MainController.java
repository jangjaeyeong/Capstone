package com.capstone.CapstoneProject;

import com.capstone.CapstoneProject.AI.*;
import com.capstone.CapstoneProject.AI.DTO.ChatRequestDTO;
import com.capstone.CapstoneProject.AI.DTO.FrontResponseDTO;
import com.capstone.CapstoneProject.AI.DTO.InterviewQuestionResDTO;
import com.capstone.CapstoneProject.AI.DTO.QuizRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final AiService aiService;

    @PostMapping("/api/error")
    String error() {
        return "ExceptionHandler";
    }

    @PostMapping("/api/result")
    public ResponseEntity<FrontResponseDTO> aiCalling
            (@RequestBody QuizRequestDto reqDto) {
        System.out.println(reqDto.getAnswers());
        FrontResponseDTO responseAI = aiService.quizService(reqDto);
        System.out.println(responseAI);
        return ResponseEntity.ok(responseAI);
    }

    @PostMapping("/api/chat")
    Map<String, String> aiChat(@RequestBody ChatRequestDTO chatReqDto) {
        System.out.println("Message" + chatReqDto.getMessages());
        System.out.println("Answer: " + chatReqDto.getQuizAnswers());
        if(chatReqDto.getNickname() == null || chatReqDto.getNickname().equals("")) {
            chatReqDto.setNickname("사용자");
        }
        Map<String, String> chat = aiService.chatService(chatReqDto);
        return chat;
    }

    @GetMapping("api/interview/questions")
    ResponseEntity<List<InterviewQuestionResDTO>> questionList() {

        List<InterviewQuestionResDTO> questions =  aiService.interviewQuestionList();
        return ResponseEntity.ok(questions);
    }
}