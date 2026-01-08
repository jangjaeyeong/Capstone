package com.capstone.CapstoneProject.Community.TeamProject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectEditDTO {
    private String field;
    private String location;
    private Integer maxPersonnel;
    private String title;
    private String content;

}
