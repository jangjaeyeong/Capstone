package com.capstone.CapstoneProject.AiCaller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnswerDto {
    private int index;
    private String id;
    private String question;
    private boolean multiple;
    private Object value;
}

