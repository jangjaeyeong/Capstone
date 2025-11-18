package com.capstone.CapstoneProject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/signIn")
    String signIn() {
        return "signin";
    }

    @GetMapping("/mainPage")
    String mainPage() {
        return "mainPage";
    }

    @GetMapping("/portfolio")
    String portfolio() {
        return "portfolio";
    }

    @GetMapping("/teamproject")
    String teamProject() {
        return "teamproject";
    }

    @GetMapping("/select_option")
    String selectOption() {
        return "select_option";

    }

    @PostMapping("/error")
    String error() {
        return "ExceptionHandler";
    }
}

