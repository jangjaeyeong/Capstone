package com.capstone.CapstoneProject.Member.Login.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
    private static final Map<String, VerificationInfo> verificationCodes = new ConcurrentHashMap<>();

    //인증코드 자동 생성
    public int createNumber(String mail) {
         int number = new Random().nextInt(900000) + 100000;
         VerificationInfo info = new VerificationInfo(number, LocalDateTime.now(), false);
         verificationCodes.put(mail, info);
         return number;
    }

    // 이메일 전송
    @Override
    public MimeMessage createMail(String mail) {
            createNumber(mail);
             MimeMessage message = javaMailSender.createMimeMessage();

             try{
                 MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                 helper.setFrom(senderEmail);
                 helper.setTo(mail);
                 helper.setSubject("이메일 인증번호");
                 String body = "<h2>Provi에 오신걸 환영합니다!</h2><h3>아래의 인증번호를 입력하세요.</h3><h1>" + verificationCodes.get(mail).getCode() + "</h1><h3>감사합니다.</h3>";
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
        return CompletableFuture.completedFuture(verificationCodes.get(mail).getCode());
    }

    @Override
    public void removeVerification(String mail) {
        verificationCodes.remove(mail);
    }

    @Override
    public VerificationInfo getVerificationInfo(String mail) {
        VerificationInfo info = verificationCodes.get(mail);
        if(info ==  null) {
            throw new IllegalArgumentException("인증 정보가 없습니다. 다시 인증해주세요.");
        }
        return info;
    }

    //메일 인증 코드 검증
    @Override
    public boolean verifyCode(String mail, int code) {
        Integer storedCode = verificationCodes.get(mail).getCode();
        VerificationInfo info = verificationCodes.get(mail);

        if(storedCode != null && storedCode == code) {
            info.setVerified(true);
            System.out.println("mailService--------------" + mail);
            System.out.println(info.getCode());
            System.out.println(info.isVerified());
            return true;
        }else {
            throw new IllegalArgumentException("인증번호가 틀렸습니다.");
        }
    }
}
