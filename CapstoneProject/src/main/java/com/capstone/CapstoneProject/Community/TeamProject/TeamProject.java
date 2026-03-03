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
    private Long id;         //고유 ID
    private String category;   //분야
    private int userLimit;   //최대 인원
    private String title;       //제목
    private String content;     //내용
    private String state = "모집중";   //현재 상태(모집중, 마감)
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount = 0;  //조회수
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;      //작성자

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;      //등록 날짜
    @LastModifiedDate
    private LocalDateTime modifiedDate;     //수정 날짜

    @Builder
    public TeamProject (String category, int userLimit, String title,
                        String content, Member writer) {
        this.category = category;
        this.userLimit = userLimit;
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

       if(editDTO.getUserLimit() != null) {
            this.userLimit = editDTO.getUserLimit  ();
       }
       if(editDTO.getCategory() != null && !editDTO.getCategory().equals("")) {
           this.category = editDTO.getCategory();
       }

    }
}
