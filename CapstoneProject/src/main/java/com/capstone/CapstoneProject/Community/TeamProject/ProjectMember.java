package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    private String role;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinDate;

    public ProjectMember(Member user, TeamProject project, String role) {
        this.user = user;
        this.project = project;
        this.role = role;
    }

}
