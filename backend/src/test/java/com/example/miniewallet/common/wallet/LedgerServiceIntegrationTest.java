package com.example.miniewallet.common.wallet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.exception.DuplicateRequestException;
import com.example.miniewallet.common.exception.InsufficientFundsException;
import com.example.miniewallet.common.repository.UserRepository;

@SpringBootTest
@Transactional
class LedgerServiceIntegrationTest {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private UserRepository users;

    @Autowired
    private WalletRepository wallets;

    @Autowired
    private TransactionRepository transactions;

    private static final AtomicInteger PHONE_COUNTER = new AtomicInteger();

    private Wallet createFundedWallet(BigDecimal startingBalance) {
        String uniquePhone = "01" + String.format("%08d", PHONE_COUNTER.incrementAndGet());
        User user = new User("Test " + UUID.randomUUID(), UUID.randomUUID() + "@example.com",
                uniquePhone, "hashed");
        users.save(user);
        Wallet wallet = wallets.save(new Wallet(user));

        if (startingBalance.signum() > 0) {
            ledgerService.topUp(wallet.getId(), startingBalance, "seed-" + UUID.randomUUID());
        }
        return wallet;
    }

    @Test
    void topUpPersistsBalanceAndTransactionTogether() {
        Wallet wallet = createFundedWallet(BigDecimal.ZERO);

        Transaction result = ledgerService.topUp(wallet.getId(), new BigDecimal("250.00"), "topup-" + UUID.randomUUID());

        Wallet reloaded = wallets.findByUserId(wallet.getUser().getId()).orElseThrow();
        assertThat(reloaded.getBalance()).isEqualByComparingTo("250.00");

        Transaction persisted = transactions.findById(result.getId()).orElseThrow();
        assertThat(persisted.getType()).isEqualTo(TransactionType.TOPUP);
        assertThat(persisted.getReceiverWallet().getId()).isEqualTo(wallet.getId());
    }

    @Test
    void payingMoreThanBalanceFailsCleanlyAndChangesNothing() {
        Wallet sender = createFundedWallet(new BigDecimal("30.00"));
        Wallet receiver = createFundedWallet(BigDecimal.ZERO);
        String referenceId = "pay-" + UUID.randomUUID();

        assertThatThrownBy(() ->
                ledgerService.move(sender.getId(), receiver.getId(), new BigDecimal("30.01"),
                        TransactionType.PAYMENT, referenceId))
                .isInstanceOf(InsufficientFundsException.class);

        Wallet senderAfter = wallets.findByUserId(sender.getUser().getId()).orElseThrow();
        Wallet receiverAfter = wallets.findByUserId(receiver.getUser().getId()).orElseThrow();
        assertThat(senderAfter.getBalance()).isEqualByComparingTo("30.00");
        assertThat(receiverAfter.getBalance()).isEqualByComparingTo("0.00");
        assertThat(transactions.findByReferenceId(referenceId)).isEmpty();
    }

    @Test
    void repeatingATopUpWithTheSameReferenceIdDoesNotDoubleCredit() {
        Wallet wallet = createFundedWallet(BigDecimal.ZERO);
        String referenceId = "idempotent-" + UUID.randomUUID();

        ledgerService.topUp(wallet.getId(), new BigDecimal("50.00"), referenceId);
        ledgerService.topUp(wallet.getId(), new BigDecimal("50.00"), referenceId);

        Wallet reloaded = wallets.findByUserId(wallet.getUser().getId()).orElseThrow();
        assertThat(reloaded.getBalance()).isEqualByComparingTo("50.00");
    }

    @Test
    void reusingAReferenceIdWithADifferentAmountIsRejected() {
        Wallet wallet = createFundedWallet(BigDecimal.ZERO);
        String referenceId = "reused-" + UUID.randomUUID();

        ledgerService.topUp(wallet.getId(), new BigDecimal("50.00"), referenceId);

        assertThatThrownBy(() -> ledgerService.topUp(wallet.getId(), new BigDecimal("999.00"), referenceId))
                .isInstanceOf(DuplicateRequestException.class);
    }
}
