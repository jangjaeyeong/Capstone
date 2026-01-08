package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TeamProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String field;
    private String location;
    private int maxPersonnel;
    private String title;
    private String content;
    private String state = "모집중";   //현재 상태(모집중, 마감)
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime ModifiedDate;

    @Builder
    public TeamProject (String field, String location, int maxPersonnel, String title,
                    String content, Member writer) {
        this.field = field;
        this.location = location;
        this.maxPersonnel = maxPersonnel;
        this.title = title;
        this.content = content;
        this.writer = writer;
    }
    public void EditProject(ProjectEditDTO editDTO) {
       if(editDTO.getTitle() != null && !editDTO.getTitle().equals("")) {
           this.title = editDTO.getTitle();
       }
        if(editDTO.getContent() != null && !editDTO.getContent().equals("")) {
            this.content = editDTO.getContent();
        }
       if (editDTO.getLocation() != null && !editDTO.getLocation().equals("")) {
           this.location = editDTO.getLocation();
       }
       if(editDTO.getMaxPersonnel() != null) {
            this.maxPersonnel = editDTO.getMaxPersonnel();
       }
       if(editDTO.getField() != null && !editDTO.getField().equals("")) {
           this.field = editDTO.getField();
       }

    }
}
