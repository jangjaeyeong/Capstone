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

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;
    @NotBlank(message = "아이디를 입력해주세요.")
    private String userId;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 2, max = 20)
    private  String password;
    @NotBlank(message = "전화번호를 입력해주세요.")
    private String cellphone;
    @NotBlank(message = "닉네임 입력해주세요")
    private String profileName;
    @NotBlank(message = "비밀번호를 다시 입력해주세요.")
    private String confirmPassword;


    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        return password.equals(confirmPassword);
    }
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Builder
    public Member toEntity() {
        if(cellphone.length() != 11) {
            throw new IllegalArgumentException("전화번호는 010을 포함한 11자리여야 합니다.");
        }
            String formatCellphone = cellphone.replaceFirst(
                    "(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");

        return Member.builder()
                .name(this.name)
                .userID(this.userId)
                .password(passwordEncoder.encode(this.password))
                .cellphone(formatCellphone)
                .profileName(this.profileName)
                .build();
    }
}
