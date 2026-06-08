package com.capstone.CapstoneProject.Community.Board.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class BoardListDTO {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private String authorNickname;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<CommentsListDTO> comments;
    private int likeCount;

    public BoardListDTO(Long id, String title, String content, int viewCount,
                        String authorNickname, LocalDateTime createdDate,
                        LocalDateTime modifiedDate, int likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.authorNickname = authorNickname;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.likeCount = likeCount;
    }
    public BoardListDTO(Long id, String title, String content, int viewCount,
                        String authorNickname, LocalDateTime createdDate, LocalDateTime modifiedDate,
                        List<CommentsListDTO> comments, int likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.authorNickname = authorNickname;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.comments = comments;
        this.likeCount = likeCount;
    }
}
