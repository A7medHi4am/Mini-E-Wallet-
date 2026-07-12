package com.example.miniewallet.common.wallet;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniewallet.common.exception.DuplicateRequestException;
import com.example.miniewallet.common.exception.WalletNotFoundException;

/**
 * Shared implementation: the only class allowed to mutate Wallet balance.
 */
@Service
public class LedgerServiceImpl implements LedgerService {

    private final WalletRepository wallets;
    private final TransactionRepository transactions;

    public LedgerServiceImpl(WalletRepository wallets, TransactionRepository transactions) {
        this.wallets = wallets;
        this.transactions = transactions;
    }

    @Override
    @Transactional
    public Transaction topUp(Long walletId, BigDecimal amount, String referenceId) {
        Transaction existing = transactions.findByReferenceId(referenceId).orElse(null);
        if (existing != null) {
            return sameOperationOrThrow(existing, TransactionType.TOPUP, null, walletId, amount, referenceId);
        }

        Wallet wallet = wallets.findByIdForUpdate(walletId)
                .orElseThrow(() -> WalletNotFoundException.forWalletId(walletId));

        wallet.credit(amount);
        wallets.save(wallet);

        return transactions.save(new Transaction(TransactionType.TOPUP, null, wallet, amount, referenceId));
    }

    @Override
    @Transactional
    public Transaction move(Long senderWalletId, Long receiverWalletId, BigDecimal amount,
                             TransactionType type, String referenceId) {
        if (senderWalletId.equals(receiverWalletId)) {
            throw new IllegalArgumentException("Cannot move funds to the same wallet");
        }

        Transaction existing = transactions.findByReferenceId(referenceId).orElse(null);
        if (existing != null) {
            return sameOperationOrThrow(existing, type, senderWalletId, receiverWalletId, amount, referenceId);
        }

        boolean senderFirst = senderWalletId < receiverWalletId;
        Long firstId = senderFirst ? senderWalletId : receiverWalletId;
        Long secondId = senderFirst ? receiverWalletId : senderWalletId;

        Wallet first = wallets.findByIdForUpdate(firstId)
                .orElseThrow(() -> WalletNotFoundException.forWalletId(firstId));
        Wallet second = wallets.findByIdForUpdate(secondId)
                .orElseThrow(() -> WalletNotFoundException.forWalletId(secondId));

        Wallet sender = senderFirst ? first : second;
        Wallet receiver = senderFirst ? second : first;

        sender.debit(amount);
        receiver.credit(amount);
        wallets.save(sender);
        wallets.save(receiver);

        return transactions.save(new Transaction(type, sender, receiver, amount, referenceId));
    }

    private Transaction sameOperationOrThrow(Transaction existing, TransactionType type,
                                              Long senderWalletId, Long receiverWalletId,
                                              BigDecimal amount, String referenceId) {
        Long existingSenderId = existing.getSenderWallet() == null ? null : existing.getSenderWallet().getId();
        boolean matches = existing.getType() == type
                && Objects.equals(existingSenderId, senderWalletId)
                && Objects.equals(existing.getReceiverWallet().getId(), receiverWalletId)
                && existing.getAmount().compareTo(amount) == 0;

        if (!matches) {
            throw new DuplicateRequestException(referenceId);
        }
        return existing;
    }
}
