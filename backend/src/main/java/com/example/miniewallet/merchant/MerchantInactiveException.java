package com.example.miniewallet.merchant;

public class MerchantInactiveException extends RuntimeException {

    public MerchantInactiveException(Long merchantId) {
        super("Merchant " + merchantId + " is not active and cannot accept payments");
    }
}
