package com.capstone.CapstoneProject.Community.Board.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentsListDTO {
    private Long id;
    private String content;
    private Long postId;
    private String authorNickname;
    private boolean anonymous;
    private LocalDateTime createdDate;

}
