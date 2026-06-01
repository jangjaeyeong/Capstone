package com.capstone.CapstoneProject.Community.TeamProject.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TeamProject project;
    private String tag;

    @Builder
    public Tags(TeamProject project, String tag) {
        this.project = project;
        this.tag = tag;
    }
}
