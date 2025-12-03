package com.capstone.CapstoneProject.AICalling;

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
