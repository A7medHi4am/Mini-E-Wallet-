package com.example.miniewallet.history;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.miniewallet.common.repository.TransactionReadRepository;
import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.wallet.TransactionType;

@Service
public class TransactionHistoryService {

    private final TransactionReadRepository transactions;

    public TransactionHistoryService(TransactionReadRepository transactions) {
        this.transactions = transactions;
    }

    public Page<Transaction> getHistory(Long walletId, TransactionType type,
                                         LocalDate dateFrom, LocalDate dateTo, Pageable pageable) {
        Instant from = dateFrom == null ? null : dateFrom.atStartOfDay(ZoneOffset.UTC).toInstant();
        // dateTo is inclusive from the caller's point of view, so the exclusive
        // upper bound is the start of the following day.
        Instant to = dateTo == null ? null : dateTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        if (from == null && to == null) {
            return transactions.findForWallet(walletId, type, pageable);
        }
        if (from != null && to == null) {
            return transactions.findForWalletFrom(walletId, type, from, pageable);
        }
        if (from == null) {
            return transactions.findForWalletBefore(walletId, type, to, pageable);
        }
        return transactions.findForWalletBetween(walletId, type, from, to, pageable);
    }
}
