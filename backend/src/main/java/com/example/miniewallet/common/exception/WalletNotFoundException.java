package com.example.miniewallet.common.exception;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(Long userId) {
        super("No wallet found for user id: " + userId);
    }
}
