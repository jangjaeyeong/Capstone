package com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListDTO {
    private Long id;
    private String category;
    private int userLimit;
    private String title;
    private String content;
    private String subStatus;
    private int viewCount;
    private String leaderName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<String> tags;
    private List<String> needRoles;
    private List<ListMembersDTO> members;
    private List<ResRecruitmentDTO> recruitments;

    public ProjectListDTO(Long id, String category, int userLimit, String title,
                          String content, String subStatus,
                          String leaderName, LocalDateTime createdDate, LocalDateTime modifiedDate,
                          List<String> tags, List<String> needRoles, List<ListMembersDTO> members,
                          List<ResRecruitmentDTO> recruitments) {
        this.id = id;
        this.category = category;
        this.userLimit = userLimit;
        this.title = title;
        this.content = content;
        this.subStatus = subStatus;
        this.leaderName = leaderName;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.tags = tags;
        this.needRoles = needRoles;
        this.members = members;
        this.recruitments = recruitments;
    }
}
