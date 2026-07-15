package com.example.miniewallet.wallet;

import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

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

    @PostMapping("/topup/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> createCheckout(@Valid @RequestBody CheckoutRequest request) {
        Wallet wallet = currentWallet();

        // Stripe wants an integer amount in the currency's smallest unit
        // (e.g. piastres for EGP), never a decimal.
        long minorUnits = request.amount()
                .movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(minorUnits)
                .setCurrency(wallet.getCurrency().toLowerCase())
                // wallet_id round-trips back to us on the webhook event —
                // it's how we know which wallet to credit once Stripe confirms payment.
                .putMetadata("wallet_id", wallet.getId().toString())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            return ResponseEntity.ok(ApiResponse.ok(new CheckoutResponse(intent.getClientSecret())));
        } catch (StripeException e) {
            throw new StripeCheckoutException(e.getMessage());
        }
    }

    @GetMapping("/topup/checkout/{referenceId}/status")
    public ResponseEntity<ApiResponse<CheckoutStatusResponse>> checkoutStatus(@PathVariable String referenceId) {
        Wallet wallet = currentWallet();

        // "succeeded" here means the webhook actually landed and credited this
        // wallet — not just that Stripe's confirmPayment() returned on the client.
        boolean credited = transactionRepository.findByReferenceId(referenceId)
                .filter(t -> t.getReceiverWallet() != null && t.getReceiverWallet().getId().equals(wallet.getId()))
                .isPresent();

        return ResponseEntity.ok(ApiResponse.ok(new CheckoutStatusResponse(credited ? "succeeded" : "pending")));
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

    @ExceptionHandler(StripeCheckoutException.class)
    public ResponseEntity<ApiResponse<Void>> handleStripeCheckout(StripeCheckoutException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiResponse.error(ex.getMessage()));
    }
}
