package com.capstone.CapstoneProject.Member.Login.Email;

import com.capstone.CapstoneProject.Member.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService implements MailServiceInterface{
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    private static final Map<String, Integer> verificationCodes = new ConcurrentHashMap<>();

    //인증코드 자동 생성
    public static int createNumber(String mail) {
         int number = new Random().nextInt(900000) + 100000;
         verificationCodes.put(mail, number);
         return number;
    }

    // 이메일 전송
    @Override
    public MimeMessage createMail(String mail) {
             int number  = createNumber(mail);
             MimeMessage message = javaMailSender.createMimeMessage();

             try{
                 MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                 helper.setFrom(senderEmail);
                 helper.setTo(mail);
                 helper.setSubject("이메일 인증번호");
                 String body = "<h2>Provi에 오신걸 환영합니다!</h2><h3>아래의 인증번호를 입력하세요.</h3><h1>" + verificationCodes.get(mail) + "</h1><h3>감사합니다.</h3>";
                 helper.setText(body, true);

             }catch (MessagingException e) {
                 e.printStackTrace();
             }
             return message;
    }


    //createMail의 내용을 이메일 전송
    @Async
    @Override
    public CompletableFuture<Integer> sendMail(String mail) {
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);
        return CompletableFuture.completedFuture(verificationCodes.get(mail));
    }

    //메일 인증 코드 검증
    @Override
    public boolean verifyCode(String mail, int code) {
        Integer storedCode = verificationCodes.get(mail);
        return storedCode != null && storedCode == code;
    }
}
