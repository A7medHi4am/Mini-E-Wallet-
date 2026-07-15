package com.example.miniewallet.admin;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.wallet.TransactionStatus;
import com.example.miniewallet.common.wallet.TransactionType;

public record TransactionAdminResponse(
        Long id,
        TransactionType type,
        Long senderWalletId,
        Long receiverWalletId,
        BigDecimal amount,
        TransactionStatus status,
        String referenceId,
        Instant createdAt) {

    public static TransactionAdminResponse from(Transaction transaction) {
        Long senderWalletId = transaction.getSenderWallet() == null ? null : transaction.getSenderWallet().getId();
        return new TransactionAdminResponse(
                transaction.getId(),
                transaction.getType(),
                senderWalletId,
                transaction.getReceiverWallet().getId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getReferenceId(),
                transaction.getCreatedAt());
    }
}
