package com.example.userservice.dto;

import javax.persistence.Id;

public class UserDTO {

    @Id
    String uuid;
    String name;
    String phoneNumber;
    String email;
    String passwordHash;
    String userType;

}
