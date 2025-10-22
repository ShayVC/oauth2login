package com.example.oauth2login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String handleError() {
        // You can also log or handle different error codes here later.
        return "error"; // This points to error.html in templates/
    }
}
