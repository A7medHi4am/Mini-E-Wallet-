package com.example.miniewallet.history;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.wallet.TransactionStatus;
import com.example.miniewallet.common.wallet.TransactionType;
import com.example.miniewallet.common.wallet.Wallet;


public record TransactionHistoryItemResponse(
        Long id,
        TransactionType type,
        BigDecimal amount,
        String direction,
        String counterpartyName,
        TransactionStatus status,
        String referenceId,
        Instant createdAt) {

    public static TransactionHistoryItemResponse from(Transaction transaction, Long walletId) {
        boolean isSender = transaction.getSenderWallet() != null
                && transaction.getSenderWallet().getId().equals(walletId);

        String direction = isSender ? "DEBIT" : "CREDIT";
        Wallet counterpartyWallet = isSender ? transaction.getReceiverWallet() : transaction.getSenderWallet();

        return new TransactionHistoryItemResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                direction,
                resolveCounterpartyName(counterpartyWallet, transaction.getType(), isSender),
                transaction.getStatus(),
                transaction.getReferenceId(),
                transaction.getCreatedAt());
    }

    private static String resolveCounterpartyName(Wallet wallet, TransactionType type, boolean isSender) {
        if (wallet == null) {
            if (type == TransactionType.TOPUP) {
                return "Top-up";
            }
            return "System";
        }
        if (wallet.getUser() != null) {
            return wallet.getUser().getName();
        }
        if (wallet.getMerchant() != null) {
            return wallet.getMerchant().getName();
        }
        return isSender ? "Unknown recipient" : "Unknown sender";
    }
}
