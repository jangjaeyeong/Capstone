package com.capstone.CapstoneProject.Member.Login.Email;

import lombok.Data;

@Data
public class MailVerificationRequestDTO {
    private String email;
    private int code;
}
