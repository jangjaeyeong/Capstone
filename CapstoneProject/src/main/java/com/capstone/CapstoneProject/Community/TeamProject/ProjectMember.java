package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_project_id")
    private TeamProject project;

    private String authority;
    private String role;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinDate;

    @Builder
    public ProjectMember(Member user, TeamProject project, String authority, String role) {
        this.user = user;
        this.project = project;
        this.authority = authority;
        this.role = role;
    }

}
