package com.example.miniewallet.common.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long walletId, BigDecimal requested, BigDecimal available) {
        super("Wallet " + walletId + " has insufficient funds: requested " + requested
                + ", available " + available);
    }
}
