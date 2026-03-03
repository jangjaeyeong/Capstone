package com.capstone.CapstoneProject.Community.TeamProject;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectDetailDTO {
    private Long id;
    private String category;
    private int userLimit;
    private String title;
    private String content;
    private String state;
    private int viewCount;
    private String writer;
    private LocalDateTime created;
    private LocalDateTime modified;

    public ProjectDetailDTO(TeamProject teamProject) {
        this.id = teamProject.getId();
        this.category = teamProject.getCategory();
        this.userLimit = teamProject.getUserLimit();
        this.title = teamProject.getTitle();
        this.content = teamProject.getContent();
        this.state = teamProject.getState();
        this.viewCount = teamProject.getViewCount();

        this.created = teamProject.getCreatedDate();
        this.modified = teamProject.getModifiedDate();
        if(teamProject.getWriter().getProfileName() != null) {
            this.writer = teamProject.getWriter().getProfileName();
        }else {
            this.writer = "알 수 없음";
        }
    }
}
