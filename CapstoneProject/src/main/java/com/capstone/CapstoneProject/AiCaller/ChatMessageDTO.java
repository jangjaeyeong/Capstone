package com.capstone.CapstoneProject.AiCaller;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatMessageDTO {
    private String role;
    private String content;
}
