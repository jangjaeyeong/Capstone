package com.capstone.CapstoneProject.Community.TeamProject.Entity;

import com.capstone.CapstoneProject.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
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
