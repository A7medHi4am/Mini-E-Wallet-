package com.example.miniewallet.common.wallet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.exception.DuplicateRequestException;

@ExtendWith(MockitoExtension.class)
class LedgerServiceImplTest {

    @Mock
    private WalletRepository wallets;

    @Mock
    private TransactionRepository transactions;

    private LedgerServiceImpl ledgerService;

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerServiceImpl(wallets, transactions);
    }

    private void stubSaves() {
        when(wallets.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactions.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private Wallet newWallet() {
        User user = new User("Test User", "test" + System.nanoTime() + "@example.com", "0100000000", "hashed");
        return new Wallet(user);
    }

    @Test
    void topUpCreditsWalletAndRecordsTopupTransaction() {
        stubSaves();
        Wallet wallet = newWallet();
        when(transactions.findByReferenceId("ref-1")).thenReturn(Optional.empty());
        when(wallets.findByIdForUpdate(7L)).thenReturn(Optional.of(wallet));

        Transaction result = ledgerService.topUp(7L, new BigDecimal("100.00"), "ref-1");

        assertThat(wallet.getBalance()).isEqualByComparingTo("100.00");
        assertThat(result.getType()).isEqualTo(TransactionType.TOPUP);
        assertThat(result.getSenderWallet()).isNull();
        assertThat(result.getReceiverWallet()).isSameAs(wallet);
        assertThat(result.getAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void topUpWithRepeatedReferenceIdAndMatchingParamsReturnsSameTransactionWithoutDoubleCrediting() {
        Wallet wallet = newWallet();
        wallet.credit(new BigDecimal("100.00"));
        Transaction existing = new Transaction(TransactionType.TOPUP, null, wallet, new BigDecimal("100.00"), "ref-1");
        when(transactions.findByReferenceId("ref-1")).thenReturn(Optional.of(existing));

        Transaction result = ledgerService.topUp(wallet.getId(), new BigDecimal("100.00"), "ref-1");

        assertThat(result).isSameAs(existing);
        assertThat(wallet.getBalance()).isEqualByComparingTo("100.00");
        verify(wallets, never()).findByIdForUpdate(any());
    }

    @Test
    void topUpWithRepeatedReferenceIdButDifferentAmountThrowsDuplicateRequestException() {
        Wallet wallet = newWallet();
        Transaction existing = new Transaction(TransactionType.TOPUP, null, wallet, new BigDecimal("100.00"), "ref-1");
        when(transactions.findByReferenceId("ref-1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> ledgerService.topUp(wallet.getId(), new BigDecimal("999.00"), "ref-1"))
                .isInstanceOf(DuplicateRequestException.class);
    }

    @Test
    void moveDebitsSenderAndCreditsReceiver() {
        stubSaves();
        Wallet sender = newWallet();
        sender.credit(new BigDecimal("100.00"));
        Wallet receiver = newWallet();

        when(transactions.findByReferenceId("ref-2")).thenReturn(Optional.empty());
        when(wallets.findByIdForUpdate(3L)).thenReturn(Optional.of(receiver));
        when(wallets.findByIdForUpdate(5L)).thenReturn(Optional.of(sender));

        Transaction result = ledgerService.move(5L, 3L, new BigDecimal("40.00"), TransactionType.TRANSFER, "ref-2");

        assertThat(sender.getBalance()).isEqualByComparingTo("60.00");
        assertThat(receiver.getBalance()).isEqualByComparingTo("40.00");
        assertThat(result.getSenderWallet()).isSameAs(sender);
        assertThat(result.getReceiverWallet()).isSameAs(receiver);
        assertThat(result.getType()).isEqualTo(TransactionType.TRANSFER);
    }

    @Test
    void moveLocksWalletsInAscendingIdOrderRegardlessOfSenderReceiverRoles() {
        stubSaves();
        Wallet sender = newWallet();
        sender.credit(new BigDecimal("100.00"));
        Wallet receiver = newWallet();

        when(transactions.findByReferenceId("ref-3")).thenReturn(Optional.empty());
        when(wallets.findByIdForUpdate(3L)).thenReturn(Optional.of(receiver));
        when(wallets.findByIdForUpdate(5L)).thenReturn(Optional.of(sender));

        ledgerService.move(5L, 3L, new BigDecimal("10.00"), TransactionType.PAYMENT, "ref-3");

        InOrder order = inOrder(wallets);
        order.verify(wallets).findByIdForUpdate(3L);
        order.verify(wallets).findByIdForUpdate(5L);
    }

    @Test
    void moveToSameWalletIsRejected() {
        assertThatThrownBy(() -> ledgerService.move(4L, 4L, new BigDecimal("10.00"), TransactionType.TRANSFER, "ref-4"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
