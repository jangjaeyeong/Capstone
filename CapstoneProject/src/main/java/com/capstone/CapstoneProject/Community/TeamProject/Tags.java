package com.capstone.CapstoneProject.Community.TeamProject;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_project_id")
    private TeamProject project;
    private String tag;

    @Builder
    public Tags(TeamProject project, String tag) {
        this.project = project;
        this.tag = tag;
    }
}
