package com.example.miniewallet.common.exception;

public class WalletFrozenException extends RuntimeException {

    public WalletFrozenException(Long walletId) {
        super("Wallet " + walletId + " is frozen");
    }
}
