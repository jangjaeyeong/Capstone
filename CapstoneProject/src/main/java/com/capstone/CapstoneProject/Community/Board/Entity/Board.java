package com.capstone.CapstoneProject.Community.Board.Entity;

import com.capstone.CapstoneProject.Community.Board.DTO.BoardEditDTO;
import com.capstone.CapstoneProject.Community.Post;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.bind.annotation.RequestBody;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
public class Board extends Post {
    private String category;
    private boolean anonymous;
    private int recommend;

    public void editPost(BoardEditDTO boardEditDTO) {
        System.out.println("제목 :::: " +  boardEditDTO.getTitle());
        System.out.println("내용 :::: " +  boardEditDTO.getContent());
        super.editPost(boardEditDTO.getTitle(), boardEditDTO.getContent());
    }

    public void increaseRecommend() {
        this.recommend++;
    }
}

