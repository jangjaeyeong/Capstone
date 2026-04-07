package com.capstone.CapstoneProject.Community.Board.Entity;

import com.capstone.CapstoneProject.Member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Member writer;
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board postId;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;      //등록 날짜
    @LastModifiedDate
    private LocalDateTime modifiedDate;
    private boolean anonymous;

}
