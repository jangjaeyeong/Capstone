package com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO;

import com.capstone.CapstoneProject.Community.TeamProject.Entity.ProjectMember;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.TeamProject;
import com.capstone.CapstoneProject.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class JoinedTeamProjectDTO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String leaderName;
    private int userLimit;
    private LocalDateTime createdAt;
    private List<String> members;

    public JoinedTeamProjectDTO(ProjectMember projectMember) {
        this.id = projectMember.getProject().getId();
        this.title = projectMember.getProject().getTitle();
        this.content = projectMember.getProject().getContent();
        this.category = projectMember.getProject().getCategory();
        this.leaderName = projectMember.getAuthority();
        this.userLimit = projectMember.getProject().getUserLimit();
        this.createdAt = projectMember.getProject().getCreatedDate();
//        this.members = allMembers.stream().map(pm -> pm.getUser().getProfileName()).toList();

    }
}
