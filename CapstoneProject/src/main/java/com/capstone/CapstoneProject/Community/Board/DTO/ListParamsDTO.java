package com.capstone.CapstoneProject.Community.Board.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ListParamsDTO {
    private int page = 1;
    private String category = "ALL";
    private String keyword = "";
}
