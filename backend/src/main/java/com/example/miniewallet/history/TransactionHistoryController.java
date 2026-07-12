package com.example.miniewallet.history;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.exception.WalletNotFoundException;
import com.example.miniewallet.common.security.CurrentUserResolver;
import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.wallet.TransactionType;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;
import com.example.miniewallet.common.web.ApiResponse;
import com.example.miniewallet.common.web.PageResponse;

@RestController
@RequestMapping("/api/history")
public class TransactionHistoryController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final TransactionHistoryService historyService;
    private final WalletRepository wallets;
    private final CurrentUserResolver currentUserResolver;

    public TransactionHistoryController(TransactionHistoryService historyService, WalletRepository wallets,
                                         CurrentUserResolver currentUserResolver) {
        this.historyService = historyService;
        this.wallets = wallets;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TransactionHistoryItemResponse>>> history(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {

        Wallet wallet = currentWallet();
        Page<Transaction> result = historyService.getHistory(
                wallet.getId(), type, from, to, PageRequest.of(page, cappedSize(size)));

        Page<TransactionHistoryItemResponse> mapped = result.map(t ->
                TransactionHistoryItemResponse.from(t, wallet.getId()));

        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(mapped)));
    }

    private Wallet currentWallet() {
        Long userId = currentUserResolver.currentUserId();
        return wallets.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));
    }

    private int cappedSize(int size) {
        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
