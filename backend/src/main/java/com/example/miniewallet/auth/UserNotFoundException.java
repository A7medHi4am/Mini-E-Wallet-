package com.example.miniewallet.auth;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("No user found for id: " + userId);
    }
}
