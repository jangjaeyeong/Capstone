package com.capstone.CapstoneProject.AICalling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    private List<ChatMessageDTO> messages;
    private List<QuizAnswerDto> quizAnswers;
    private String nickname;
}
