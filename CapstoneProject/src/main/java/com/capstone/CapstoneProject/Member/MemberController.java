package com.capstone.CapstoneProject.Member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Component
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/userInsert")
    ResponseEntity<String> userInsert(@Valid @RequestBody MemberDTO memberDTO) {

            memberService.saveMember (memberDTO);

        return ResponseEntity.ok("회원가입 성공");
    }

    ResponseEntity<Map> responseNickname(Map<String, String> nickname) {
        System.out.println(nickname);
        return ResponseEntity.ok(nickname);
    }
}