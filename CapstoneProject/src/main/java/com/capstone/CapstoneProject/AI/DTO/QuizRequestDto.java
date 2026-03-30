package com.capstone.CapstoneProject.AI.DTO;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizRequestDto {
    private List<QuizAnswerDto> answers;
}
