package com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListMembersDTO {
    private String userName;
    private boolean isLeader;
    private String role;

}
