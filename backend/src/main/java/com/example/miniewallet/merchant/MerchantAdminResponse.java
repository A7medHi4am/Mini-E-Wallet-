package com.example.miniewallet.merchant;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.miniewallet.common.domain.Merchant;
import com.example.miniewallet.common.wallet.Wallet;

public record MerchantAdminResponse(
        Long id,
        String name,
        String category,
        boolean active,
        Instant createdAt,
        Long walletId,
        BigDecimal balance,
        String currency) {

    public static MerchantAdminResponse from(Merchant merchant, Wallet wallet) {
        return new MerchantAdminResponse(
                merchant.getId(), merchant.getName(), merchant.getCategory(), merchant.isActive(),
                merchant.getCreatedAt(), wallet.getId(), wallet.getBalance(), wallet.getCurrency());
    }
}
