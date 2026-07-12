package com.example.miniewallet.merchant;

public class SelfTransferException extends RuntimeException {

    public SelfTransferException() {
        super("Cannot transfer money to yourself");
    }
}
