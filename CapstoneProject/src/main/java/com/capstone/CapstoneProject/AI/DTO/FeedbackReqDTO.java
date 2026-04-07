package com.capstone.CapstoneProject.AI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackReqDTO {
    private Long questionId;
    private String answer;
}
