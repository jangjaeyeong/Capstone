package com.capstone.CapstoneProject.Member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/userInsert")
    ResponseEntity<String> userInsert(@Valid @RequestBody MemberDTO memberDTO) {

//        Member member = memberDTO.toEntity();
            memberService.saveMember (memberDTO);

        return ResponseEntity.ok("회원가입 성공");
    }
}