package com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResRecruitmentDTO {
    private String role;
    private Integer count;

    public ResRecruitmentDTO(String needRoles, Integer count) {
        this.role = needRoles;
        this.count = count;
    }
}
