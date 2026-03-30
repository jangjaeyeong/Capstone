package com.capstone.CapstoneProject.Community.TeamProject.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectJoinDTO {
    private Long projectId;
    private String role;
}
