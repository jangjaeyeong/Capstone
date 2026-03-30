package com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResRecruitmentDTO {
    private String role;
    private Integer recruitments;

    public ResRecruitmentDTO(String needRoles, Integer recruitments) {
        this.role = needRoles;
        this.recruitments = recruitments;
    }
}
