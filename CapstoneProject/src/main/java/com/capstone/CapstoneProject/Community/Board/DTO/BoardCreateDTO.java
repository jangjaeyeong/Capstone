package com.capstone.CapstoneProject.Community.Board.DTO;

import com.capstone.CapstoneProject.Community.Board.Entity.Board;
import com.capstone.CapstoneProject.Member.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateDTO {
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    @NotBlank(message = "카테고리를 선택해주세요")
    private String category;
    private boolean anonymous;

    public Board toEntity(Member writer){
        System.out.println("title : " + this.title);
        System.out.println("content : " + this.content);
        System.out.println("category : " + this.category);
        System.out.println("anonymous : " + this.anonymous);
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .writer(writer)
                .category(this.category)
                .anonymous(this.anonymous)
                .build();
    }
}
