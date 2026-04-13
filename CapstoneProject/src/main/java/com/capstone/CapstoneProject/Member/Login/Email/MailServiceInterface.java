package com.capstone.CapstoneProject.Member.Login.Email;

import jakarta.mail.internet.MimeMessage;

import java.util.concurrent.CompletableFuture;

public interface MailServiceInterface {

    MimeMessage createMail(String mail);
    boolean verifyCode(String email, int code);
    CompletableFuture<Integer> sendMail(String mail);
}
