package com.capstone.CapstoneProject.AI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResDTO {
    private String summary;
    private String strengths;
    private String improvements;
    private String tips;

}
