package com.example.miniewallet.merchant;

public class RecipientNotFoundException extends RuntimeException {

    public RecipientNotFoundException(String identifier) {
        super("No user found with email or phone: " + identifier);
    }
}
