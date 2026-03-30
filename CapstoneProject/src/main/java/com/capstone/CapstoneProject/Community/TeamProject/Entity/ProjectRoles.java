package com.capstone.CapstoneProject.Community.TeamProject.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ProjectRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "team_project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamProject project;
    private String needRoles;
    private Integer recruitments;

    @Builder
    public ProjectRoles (TeamProject project, String needRoles, int recruitments) {
        this.project = project;
        this.needRoles = needRoles;
        this.recruitments = recruitments;
    }
}
