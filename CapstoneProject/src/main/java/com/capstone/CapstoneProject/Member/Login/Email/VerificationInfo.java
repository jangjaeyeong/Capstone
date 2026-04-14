package com.capstone.CapstoneProject.Member.Login.Email;

import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VerificationInfo {
    private int code;
    @CreatedDate
    private LocalDateTime createAt;
    private boolean isVerified;

    public VerificationInfo(@NotBlank(message = "이메일을 입력해주세요.") String email) {
    }
}
