package com.capstone.CapstoneProject.Community.TeamProject;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProjectDetailDTO {
    private int id;
    private String field;
    private String location;
    private int maxPersonnel;
    private String title;
    private String content;
    private String state;
    private int viewCount;
    private String writer;
    private LocalDateTime created;
    private LocalDateTime modified;

    public ProjectDetailDTO(TeamProject teamProject) {
        this.id = teamProject.getId();
        this.field = teamProject.getField();
        this.location = teamProject.getLocation();
        this.maxPersonnel = teamProject.getMaxPersonnel();
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
