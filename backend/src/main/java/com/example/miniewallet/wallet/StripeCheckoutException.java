package com.example.miniewallet.wallet;

public class StripeCheckoutException extends RuntimeException {

    public StripeCheckoutException(String message) {
        super(message);
    }
}
