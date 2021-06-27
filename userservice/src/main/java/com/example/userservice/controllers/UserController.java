package com.example.userservice.controllers;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/{name}")
    public UserResponse getUser(@PathVariable String name) {
        log.info("Users name {}", name);
        return userService.getUser(name);
    }

    @PostMapping("/")
    public ResponseEntity<Object> createUser(@RequestBody UserRequest userRequest) {
        log.info("Users Request {}", userRequest);
        return userService.createUser(userRequest);
    }
}
