package com.example.miniewallet.merchant;

public class MerchantNotFoundException extends RuntimeException {

    public MerchantNotFoundException(Long merchantId) {
        super("No merchant found with id: " + merchantId);
    }
}
