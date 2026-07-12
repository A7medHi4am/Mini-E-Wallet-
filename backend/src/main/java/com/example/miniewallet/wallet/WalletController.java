package com.example.miniewallet.wallet;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.exception.WalletNotFoundException;
import com.example.miniewallet.common.security.CurrentUserResolver;
import com.example.miniewallet.common.wallet.LedgerService;
import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.wallet.TransactionRepository;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;
import com.example.miniewallet.common.web.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private static final int RECENT_TRANSACTIONS_LIMIT = 5;

    private final LedgerService ledgerService;
    private final WalletRepository wallets;
    private final TransactionRepository transactionRepository;
    private final CurrentUserResolver currentUserResolver;

    public WalletController(LedgerService ledgerService, WalletRepository wallets,
                             TransactionRepository transactionRepository,
                             CurrentUserResolver currentUserResolver) {
        this.ledgerService = ledgerService;
        this.wallets = wallets;
        this.transactionRepository = transactionRepository;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<TransactionResponse>> topUp(@Valid @RequestBody TopUpRequest request) {
        Wallet wallet = currentWallet();
        Transaction transaction = ledgerService.topUp(wallet.getId(), request.amount(), request.referenceId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(TransactionResponse.from(transaction)));
    }

    @GetMapping("/transactions/recent")
    public ResponseEntity<ApiResponse<RecentTransactionsResponse>> recentTransactions() {
        Wallet wallet = currentWallet();
        List<TransactionResponse> recent = transactionRepository
                .findBySenderWalletIdOrReceiverWalletIdOrderByCreatedAtDesc(
                        wallet.getId(), wallet.getId(), PageRequest.of(0, RECENT_TRANSACTIONS_LIMIT))
                .stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(new RecentTransactionsResponse(wallet.getId(), recent)));
    }

    private Wallet currentWallet() {
        Long userId = currentUserResolver.currentUserId();
        return wallets.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));
    }
}
