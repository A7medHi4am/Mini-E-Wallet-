package com.example.miniewallet.common.exception;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(Long userId) {
        super("No wallet found for user id: " + userId);
    }

    public static WalletNotFoundException forWalletId(Long walletId) {
        return new WalletNotFoundException(walletId, true);
    }

    private WalletNotFoundException(Long walletId, boolean byWalletId) {
        super("No wallet found with id: " + walletId);
    }
}
