package com.capstone.CapstoneProject.AICalling;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizResponseDTO {
    private String role;
    private String summary;
    private List<String> stacks;
    private List<String> roadmap;
    private Map<String, String> reasons;
}
