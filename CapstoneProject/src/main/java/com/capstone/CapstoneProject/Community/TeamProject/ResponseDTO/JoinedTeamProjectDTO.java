package com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO;

import com.capstone.CapstoneProject.Community.TeamProject.Entity.ProjectMember;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.TeamProject;
import com.capstone.CapstoneProject.Community.TeamProject.Repository.ProjectMemberRepository;
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
    private String category;
    private String authority;
    private int userLimit;
    private LocalDateTime createdAt;
    private Long members;

    public JoinedTeamProjectDTO(ProjectMember projectMember, Long memberCount) {
        this.id = projectMember.getProject().getId();
        this.title = projectMember.getProject().getTitle();
        this.category = projectMember.getProject().getCategory();
        this.authority = projectMember.getAuthority();
        this.userLimit = projectMember.getProject().getUserLimit();
        this.createdAt = projectMember.getProject().getCreatedDate();
        this.members = memberCount;


    }
}
