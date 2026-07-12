package com.example.miniewallet.merchant;

public class MerchantHasBalanceException extends RuntimeException {

    public MerchantHasBalanceException(Long merchantId) {
        super("Merchant " + merchantId + " still has a wallet balance and cannot be deleted; deactivate it instead");
    }
}
