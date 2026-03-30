package com.capstone.CapstoneProject.Community.TeamProject.Entity;

import com.capstone.CapstoneProject.Community.Post;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectEditDTO;
import com.capstone.CapstoneProject.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class TeamProject extends Post {
    private String category;   //분야
    private int userLimit;   //최대 인원
    private String state = "모집중";   //현재 상태(모집중, 마감)

    public TeamProject (String category, int userLimit, String title,
                        String content, Member writer, List<String> tag) {
        this.category = category;
        this.userLimit = userLimit;
    }
    public void editProject(ProjectEditDTO editDTO) {
        super.editPost(editDTO.getTitle(), editDTO.getContent());
            if(editDTO.getUserLimit() != null) {
                this.userLimit = editDTO.getUserLimit();
            }
            if(editDTO.getCategory() != null && !editDTO.getCategory().equals("")) {
                this.category = editDTO.getCategory();
            }
        }
        public void editState(long projectMemberNumber) {
            if(projectMemberNumber == this.userLimit) {
                this.state = "진행중";
            }
        }
    }
