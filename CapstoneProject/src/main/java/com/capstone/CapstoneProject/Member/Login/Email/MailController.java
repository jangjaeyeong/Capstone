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
    @PostMapping("api/auth/email/send-code")
    public CompletableFuture<String> mailSend(@RequestBody MailRequestDTO mailRequestDTO){
        return mailServiceInterface.sendMail(mailRequestDTO.getEmail()).thenApply(
                number -> String.valueOf(number));
    }

    //인증번호 검증 메서드
    @PostMapping("api/auth/email/verify-code")
    public String verifyCode(@RequestBody MailVerificationRequestDTO mailVerificationRequestDTO) {
        boolean isVerified = mailServiceInterface.verifyCode(mailVerificationRequestDTO.getEmail(),
                mailVerificationRequestDTO.getCode());
        return isVerified ? "Verified" : "Verification failed";
    }
}
