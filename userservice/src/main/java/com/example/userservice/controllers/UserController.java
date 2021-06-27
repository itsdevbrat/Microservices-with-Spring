package com.example.userservice.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/")
    public String sayHi() {
        return "Hii, "+port;
    }
}
