package com.capstone.CapstoneProject.Member.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String userID;
    private String userPassword; // 👈 옛날 responseBody에 넣던 닉네임!
}