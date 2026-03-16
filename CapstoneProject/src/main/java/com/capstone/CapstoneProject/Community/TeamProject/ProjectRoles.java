package com.capstone.CapstoneProject.Community.TeamProject;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ProjectRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "team_project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamProject project;
    private String needRoles;

    @Builder
    public ProjectRoles (TeamProject project, String needRoles) {
        this.project = project;
        this.needRoles = needRoles;
    }
}
