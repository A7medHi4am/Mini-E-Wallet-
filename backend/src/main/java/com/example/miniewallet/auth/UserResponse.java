package com.example.miniewallet.auth;

import java.math.BigDecimal;

import com.example.miniewallet.common.domain.Role;
import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletStatus;



public record UserResponse(Long id, String name, String email, String phone, Role role, WalletSummary wallet) {

    public record WalletSummary(BigDecimal balance, String currency, WalletStatus status) {
        static WalletSummary from(Wallet wallet) {
            return new WalletSummary(wallet.getBalance(), wallet.getCurrency(), wallet.getStatus());
        }
    }

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole(), null);
    }

    public static UserResponse from(User user, Wallet wallet) {
        return new UserResponse(
                user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole(),
                WalletSummary.from(wallet));
    }
}
