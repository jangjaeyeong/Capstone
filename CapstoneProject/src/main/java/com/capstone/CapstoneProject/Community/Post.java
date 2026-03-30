package com.capstone.CapstoneProject.Community;

import com.capstone.CapstoneProject.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount = 0;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;      //등록 날짜
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void editPost(String title, String content) {
        if(title != null && !title.equals("")) {
            this.title = title;
        }
        if(content != null && ! content.equals("")) {
            this.content = content;
        }
    }
    public void increaseViewCount() {
        this.viewCount++;
    }
}
