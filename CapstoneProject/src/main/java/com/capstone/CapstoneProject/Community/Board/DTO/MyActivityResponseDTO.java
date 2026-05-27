package com.capstone.CapstoneProject.Community.Board.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MyActivityResponseDTO {
    private List<String> myPosting;
    private Map<String, String> myComments;
}
