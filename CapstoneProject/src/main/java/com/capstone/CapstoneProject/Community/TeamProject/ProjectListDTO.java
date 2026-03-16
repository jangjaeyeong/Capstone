package com.capstone.CapstoneProject.Community.TeamProject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListDTO {
    private String category;
    private int userLimit;
    private String title;
    private String state;
    private int viewCount;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<String> tag;
    private List<String> needRoles;

    public ProjectListDTO(String category, int userLimit, String title, String state,
                          String writer, LocalDateTime createdDate, LocalDateTime ModifiedDate,
                          List<String> tag, List<String> needRoles) {
        this.category = category;
        this.userLimit = userLimit;
        this.title = title;
        this.state = state;
        this.writer = writer;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.tag = tag;
        this.needRoles = needRoles;
    }
}
