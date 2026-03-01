package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Member.Member;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDTO {
    @NotBlank(message = "분야를 입력해주세요")
    private String category;
    @NotNull(message = "최대 인원을 입력해주세요")
    @Min(value = 1, message = "인원은 최소 1명 이상이어야 합니다")
    private int maxPersonnel;
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    public TeamProject toEntity(Member writer) {
        return TeamProject.builder()
                .category(this.category)
                .maxPersonnel(this.maxPersonnel)
                .title(this.title)
                .content(this.content)
                .writer(writer)
                .build();
    }
}
