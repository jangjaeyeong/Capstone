package com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO;

import com.capstone.CapstoneProject.Community.TeamProject.Entity.TeamProject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
    private List<String> tags;
    private List<String> needRoles;
    private List<ListMembersDTO> members;

    public ProjectDetailDTO(TeamProject teamProject, List<String> tags,
                            List<String> needRoles, List<ListMembersDTO> members) {
        this.id = teamProject.getId();
        this.category = teamProject.getCategory();
        this.userLimit = teamProject.getUserLimit();
        this.title = teamProject.getTitle();
        this.content = teamProject.getContent();
        this.state = teamProject.getState();
        this.viewCount = teamProject.getViewCount();
        this.created = teamProject.getCreatedDate();
        this.modified = teamProject.getModifiedDate();
        this.tags = tags;
        this.needRoles = needRoles;
        if(teamProject.getWriter().getProfileName() != null) {
            this.writer = teamProject.getWriter().getProfileName();
        }else {
            this.writer = "알 수 없음";
        }
        this.members = members;
    }
}
