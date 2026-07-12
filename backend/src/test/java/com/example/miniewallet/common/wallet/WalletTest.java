package com.example.miniewallet.common.wallet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.exception.InsufficientFundsException;
import com.example.miniewallet.common.exception.WalletFrozenException;

class WalletTest {

    private Wallet newActiveWallet() {
        User user = new User("Test User", "test@example.com", "0100000000", "hashed");
        return new Wallet(user);
    }

    private void freeze(Wallet wallet) throws Exception {
        Field statusField = Wallet.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(wallet, WalletStatus.FROZEN);
    }

    @Test
    void creditIncreasesBalance() {
        Wallet wallet = newActiveWallet();

        wallet.credit(new BigDecimal("100.00"));

        assertThat(wallet.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void creditRejectsNonPositiveAmount() {
        Wallet wallet = newActiveWallet();

        assertThatThrownBy(() -> wallet.credit(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> wallet.credit(new BigDecimal("-5")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void creditOnFrozenWalletThrows() throws Exception {
        Wallet wallet = newActiveWallet();
        freeze(wallet);

        assertThatThrownBy(() -> wallet.credit(new BigDecimal("10")))
                .isInstanceOf(WalletFrozenException.class);
    }

    @Test
    void debitDecreasesBalance() {
        Wallet wallet = newActiveWallet();
        wallet.credit(new BigDecimal("100.00"));

        wallet.debit(new BigDecimal("40.00"));

        assertThat(wallet.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void debitExceedingBalanceThrowsAndLeavesBalanceUnchanged() {
        Wallet wallet = newActiveWallet();
        wallet.credit(new BigDecimal("50.00"));

        assertThatThrownBy(() -> wallet.debit(new BigDecimal("50.01")))
                .isInstanceOf(InsufficientFundsException.class);
        assertThat(wallet.getBalance()).isEqualByComparingTo("50.00");
    }

    @Test
    void debitRejectsNonPositiveAmount() {
        Wallet wallet = newActiveWallet();
        wallet.credit(new BigDecimal("10"));

        assertThatThrownBy(() -> wallet.debit(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void debitOnFrozenWalletThrows() throws Exception {
        Wallet wallet = newActiveWallet();
        wallet.credit(new BigDecimal("100.00"));
        freeze(wallet);

        assertThatThrownBy(() -> wallet.debit(new BigDecimal("10")))
                .isInstanceOf(WalletFrozenException.class);
    }
}
