package com.example.userservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    String uuid;
    String name;
    String phoneNumber;
    String email;
    String passwordHash;
    String userType;



}
