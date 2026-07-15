package com.example.miniewallet.admin;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletStatus;

public record WalletAdminResponse(
        Long id,
        Long ownerId,
        String ownerName,
        BigDecimal balance,
        String currency,
        WalletStatus status,
        Instant createdAt) {

    public static WalletAdminResponse from(Wallet wallet) {
        Long ownerId = wallet.getUser() != null ? wallet.getUser().getId() :
                wallet.getMerchant() != null ? wallet.getMerchant().getId() : null;
        String ownerName = wallet.getUser() != null ? wallet.getUser().getName() :
                wallet.getMerchant() != null ? wallet.getMerchant().getName() : null;
        return new WalletAdminResponse(
                wallet.getId(),
                ownerId,
                ownerName,
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getStatus(),
                wallet.getCreatedAt());
    }
}
