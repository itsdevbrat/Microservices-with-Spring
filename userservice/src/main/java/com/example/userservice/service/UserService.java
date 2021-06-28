package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Object> createUser(UserRequest userRequest) {
        if (userRepository.findByName(userRequest.getName()) == null) {
            User user = new User();
            user.setUuid(UUID.randomUUID().toString());
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
            user.setUserType(userRequest.getUserType());
            userRepository.save(user);
            ResponseEntity<UserResponse> userRes = restTemplate
                    .exchange(String.format("http://user-service/user/%s", user.getName()), HttpMethod.GET,
                            null, UserResponse.class);

            return new ResponseEntity<>(userRes.getBody(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public UserResponse getUser(String name) {
        User user = userRepository.findByName(name);
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setUserType(user.getUserType());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setName(user.getName());
        return userResponse;
    }
}
