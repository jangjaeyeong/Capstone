package com.capstone.CapstoneProject.Member.Login.Email;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@EnableAsync
public class MailController {
    private final  MailServiceInterface mailServiceInterface;

    //인증번호 발송 메서드
    @PostMapping("/api/users/mail")
    public CompletableFuture<String> mailSend(@RequestBody MailRequestDTO mailRequestDTO){
        return mailServiceInterface.sendMail(mailRequestDTO.getMail()).thenApply(
                number -> String.valueOf(number));
    }

    //인증번호 검증 메서드
    @PostMapping("api/users/verify-code")
    public String verifyCode(@RequestBody MailVerificationRequestDTO mailVerificationRequestDTO) {
        boolean isVerified = mailServiceInterface.verifyCode(mailVerificationRequestDTO.getMail(),
                mailVerificationRequestDTO.getCode());
        return isVerified ? "Verified" : "Verification failed";
    }
}
