package com.capstone.CapstoneProject.Community.Board.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BoardListDTO {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public BoardListDTO(Long id, String title, String content, int viewCount,
                        String writer, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.writer = writer;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
