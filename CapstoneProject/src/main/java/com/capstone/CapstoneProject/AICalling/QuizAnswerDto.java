package com.capstone.CapstoneProject.AICalling;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuizAnswerDto {
    private int index;
    private String id;
    private String question;
    private boolean multiple;
    private Object value;
}

