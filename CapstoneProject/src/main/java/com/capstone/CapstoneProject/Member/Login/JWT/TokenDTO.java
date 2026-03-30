package com.capstone.CapstoneProject.Member.Login.JWT;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String token;
    private String nickname; // 👈 옛날 responseBody에 넣던 닉네임!
}