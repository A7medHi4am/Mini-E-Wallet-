package com.example.miniewallet.merchant;

import java.time.Instant;

import com.example.miniewallet.common.domain.Merchant;

public record MerchantResponse(Long id, String name, String category, boolean active, Instant createdAt) {

    public static MerchantResponse from(Merchant merchant) {
        return new MerchantResponse(
                merchant.getId(), merchant.getName(), merchant.getCategory(),
                merchant.isActive(), merchant.getCreatedAt());
    }
}
