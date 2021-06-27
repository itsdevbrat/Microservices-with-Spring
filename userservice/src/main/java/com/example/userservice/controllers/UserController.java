package com.example.userservice.controllers;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${server.port}")
    private String port;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String sayHi() {
        return "Hii, "+port;
    }

    @PostMapping("/")
    public ResponseEntity<Void> createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }
}
