package com.capstone.CapstoneProject.Community.TeamProject.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class ProjectRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "team_project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
