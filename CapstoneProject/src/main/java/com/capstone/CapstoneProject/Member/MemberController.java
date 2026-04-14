package com.capstone.CapstoneProject.Member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Component
public class MemberController {

    private final MemberService memberService;

    @PostMapping("api/auth/signup")
    ResponseEntity<String> userInsert(@Valid @RequestBody MemberDTO memberDTO) {

            memberService.saveMember (memberDTO);

        return ResponseEntity.ok("회원가입 성공");
    }
//
//    ResponseEntity<Map> responseNickname(Map<String, String> nickname) {
//        System.out.println(nickname);
//        return ResponseEntity.ok(nickname);
//    }
    @GetMapping("api/auth/check-userid")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@RequestParam(value = "userId") String userId) {
        boolean isAvailable = memberService.checkUserId(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("api/auth/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam(value = "nickname") String profileName) {
        boolean isAvailable = memberService.checkNickname(profileName);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        return ResponseEntity.ok().body(response);
    }
}