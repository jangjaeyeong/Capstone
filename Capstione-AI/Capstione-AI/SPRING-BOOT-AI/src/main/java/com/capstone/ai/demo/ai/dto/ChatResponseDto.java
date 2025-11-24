package com.capstone.ai.demo.ai.dto;

// Spring Boot가 프론트에게 돌려줄 응답 DTO
// FastAPI가 {"content": "..."} 형식으로 보내기 때문에 필드 이름도 content로 맞춤

public class ChatResponseDto {
     private String content;           // AI 최종 답변 텍스트

    public ChatResponseDto() {}       // 기본 생성자

    public ChatResponseDto(String content) {  // 편하게 생성할 수 있는 생성자
        this.content = content;
    }

    public String getContent() {      // getter
        return content;
    }

    public void setContent(String content) {  // setter (필요하면 사용)
        this.content = content;
    }
}
