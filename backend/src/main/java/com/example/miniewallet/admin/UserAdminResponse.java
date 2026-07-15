package com.example.miniewallet.admin;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.miniewallet.common.domain.Role;
import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletStatus;

public record UserAdminResponse(
        Long id,
        String name,
        String email,
        String phone,
        Role role,
        WalletSummary wallet,
        Instant createdAt) {

    public static UserAdminResponse from(User user) {
        return new UserAdminResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                null,
                user.getCreatedAt());
    }

    public static UserAdminResponse from(User user, Wallet wallet) {
        return new UserAdminResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                wallet == null ? null : WalletSummary.from(wallet),
                user.getCreatedAt());
    }

    public record WalletSummary(BigDecimal balance, String currency, WalletStatus status) {
        public static WalletSummary from(Wallet wallet) {
            return new WalletSummary(wallet.getBalance(), wallet.getCurrency(), wallet.getStatus());
        }
    }
}
