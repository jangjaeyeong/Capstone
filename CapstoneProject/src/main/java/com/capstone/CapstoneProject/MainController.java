package com.capstone.CapstoneProject;

import com.capstone.CapstoneProject.AiCaller.AiService;
import com.capstone.CapstoneProject.AiCaller.requestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            (@RequestBody requestDto reqDto) {
        System.out.println(reqDto.getAnswers());
        ResponseEntity<Map> responseAI = aiService.callingAI(reqDto);
        return ResponseEntity.ok(responseAI);
    }
}
