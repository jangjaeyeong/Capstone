package com.capstone.ai.demo.ai.dto;

public class ChatRequestDto {
     private String message;           // 사용자가 입력한 질문

    public ChatRequestDto() {}        // 기본 생성자

    public String getMessage() {      // getter
        return message;
    }

    public void setMessage(String message) {  // setter
        this.message = message;
    }
}
