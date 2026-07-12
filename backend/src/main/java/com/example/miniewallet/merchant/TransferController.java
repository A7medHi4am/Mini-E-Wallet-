package com.example.miniewallet.merchant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.security.CurrentUserResolver;
import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.web.ApiResponse;
import com.example.miniewallet.wallet.TransactionResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;
    private final CurrentUserResolver currentUserResolver;

    public TransferController(TransferService transferService, CurrentUserResolver currentUserResolver) {
        this.transferService = transferService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        Long userId = currentUserResolver.currentUserId();
        Transaction transaction = transferService.transfer(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(TransactionResponse.from(transaction)));
    }
}
