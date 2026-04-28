package com.capstone.CapstoneProject.Member;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String userId;
    @Size(min = 8, max = 20)
    private  String password;
    private String email;
    private String nickname;
    private String confirmPassword;


    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        return password.equals(confirmPassword);
    }
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Builder
    public Member LocalToEntity() {
        return Member.builder()
                .userID(this.userId)
                .password(passwordEncoder.encode(this.password))
                .email(this.email)
                .profileName(this.nickname)
                .provider("local")
                .providerCode("null")
                .build();
    }
    public Member SocialToEntity(String email, String profileName, String provider, String providerCode) {
        return Member.builder()
                .password(UUID.randomUUID().toString())
                .email(email)
                .profileName(profileName)
                .provider(provider)
                .providerCode(providerCode)
                .build();
    }
    public void updateSocialInfo(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
