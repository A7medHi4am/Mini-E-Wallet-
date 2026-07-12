package com.example.miniewallet.auth;

public class PhoneAlreadyUsedException extends RuntimeException {

    public PhoneAlreadyUsedException(String phone) {
        super("Phone already in use: " + phone);
    }
}
