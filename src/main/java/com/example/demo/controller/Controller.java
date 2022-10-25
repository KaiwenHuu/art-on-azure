package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    
    @GetMapping("/")
    public String home() {
        return "Hello from Azure App Service! Let's start connecting to Azure SQL Server!";
    }
}
