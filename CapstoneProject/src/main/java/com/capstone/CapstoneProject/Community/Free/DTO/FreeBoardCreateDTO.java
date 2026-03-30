package com.capstone.CapstoneProject.Community.Free.DTO;

import com.capstone.CapstoneProject.Community.Free.Entity.FreeBoard;
import com.capstone.CapstoneProject.Member.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeBoardCreateDTO {
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    public FreeBoard toEntity(Member writer){
        return FreeBoard.builder()
                .title(this.title)
                .content(this.content)
                .writer(writer)
                .build();
    }
}
