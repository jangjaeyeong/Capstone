package com.capstone.CapstoneProject.Member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    //입력 안 했을 때
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<String> handleValidException(MethodArgumentNotValidException ex) {
        String errorMsg =  ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMsg);
    }

    //중복 체크
    @org.springframework.web.bind.annotation.ExceptionHandler({IllegalArgumentException.class})
    ResponseEntity<String> HandleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
