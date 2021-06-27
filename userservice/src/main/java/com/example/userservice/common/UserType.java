package com.example.userservice.common;

//Only strings supported in Enum hence need to add extra code to assign int values to enum
public enum UserType {
    INVESTOR(1),
    JOB_SEEKER(2),
    ENTREPRENEUR(3);

    private final int value;

    UserType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
