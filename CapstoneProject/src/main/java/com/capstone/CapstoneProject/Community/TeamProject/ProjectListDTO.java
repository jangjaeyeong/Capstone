package com.capstone.CapstoneProject.Community.TeamProject;

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
    private String subStatus;
    private int viewCount;
    private String leaderName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<String> tags;
    private List<String> needRoles;
    private List<String> members;

    public ProjectListDTO(Long id, String category, int userLimit, String title, String subStatus,
                          String leaderName, LocalDateTime createdDate, LocalDateTime ModifiedDate,
                          List<String> tags, List<String> needRoles, List<String> members) {
        this.id = id;
        this.category = category;
        this.userLimit = userLimit;
        this.title = title;
        this.subStatus = subStatus;
        this.leaderName = leaderName;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.tags = tags;
        this.needRoles = needRoles;
        this.members = members;
    }
}
