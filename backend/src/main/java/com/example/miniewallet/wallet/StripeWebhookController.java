package com.example.miniewallet.wallet;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.wallet.LedgerService;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/api/wallet/topup")
public class StripeWebhookController {

    private final String webhookSecret;
    private final LedgerService ledgerService;
    private final WalletRepository wallets;

    public StripeWebhookController(@Value("${app.stripe.webhook-secret:}") String webhookSecret,
                                   LedgerService ledgerService,
                                   WalletRepository wallets) {
        this.webhookSecret = webhookSecret;
        this.ledgerService = ledgerService;
        this.wallets = wallets;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody byte[] payload,
                                          @RequestHeader("Stripe-Signature") String sigHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe webhook secret is not configured");
        }

        Event event;
        try {
            event = Webhook.constructEvent(new String(payload, StandardCharsets.UTF_8), sigHeader, webhookSecret);
        } catch (SignatureVerificationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Stripe signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null && "paid".equals(session.getPaymentStatus())) {
                String referenceId = session.getMetadata().get("referenceId");
                String walletIdValue = session.getMetadata().get("walletId");
                String amountValue = session.getMetadata().get("amount");
                if (referenceId != null && walletIdValue != null && amountValue != null) {
                    Long walletId = Long.valueOf(walletIdValue);
                    Wallet wallet = wallets.findById(walletId).orElse(null);
                    if (wallet != null) {
                        ledgerService.topUp(wallet.getId(), new java.math.BigDecimal(amountValue), referenceId);
                    }
                }
            }
        }

        return ResponseEntity.ok("received");
    }
}
