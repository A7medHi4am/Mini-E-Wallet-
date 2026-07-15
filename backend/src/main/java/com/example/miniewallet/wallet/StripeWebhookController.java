package com.example.miniewallet.wallet;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.wallet.LedgerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Receives server-to-server callbacks from Stripe — NOT a user-facing
 * endpoint. It carries no JWT (Stripe can't obtain one), so it's permitted
 * without auth in SecurityConfig; its actual authentication is the
 * Stripe-Signature check below, not Spring Security.
 */
@RestController
@RequestMapping("/api/wallet/topup")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final LedgerService ledgerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            HttpServletRequest request,
            @RequestHeader("Stripe-Signature") String signatureHeader) throws IOException {

        String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Rejected webhook call: signature verification failed");
            return ResponseEntity.badRequest().body("invalid signature");
        }

        log.info("Received Stripe webhook event: {} ({})", event.getType(), event.getId());

        if ("payment_intent.succeeded".equals(event.getType())) {
            // event.getDataObjectDeserializer().getObject() deserializes into the
            // SDK's typed PaymentIntent using the API version stamped on the event.
            // If that doesn't match the version this SDK build expects, it silently
            // returns empty instead of throwing — nothing credits, nothing logs,
            // nothing fails. Parsing the raw JSON we already have (for signature
            // verification) sidesteps that entirely: we only need 3 fields.
            JsonNode paymentIntentJson = objectMapper.readTree(payload).path("data").path("object");
            creditWallet(paymentIntentJson);
        }

        // Any 2xx tells Stripe "received" so it stops retrying this event.
        // Event types we don't handle (payment_intent.payment_failed, etc.)
        // are acknowledged the same way — there's nothing to do for them yet.
        return ResponseEntity.ok("received");
    }

    private void creditWallet(JsonNode paymentIntentJson) {
        String paymentIntentId = paymentIntentJson.path("id").asText();
        long amountMinorUnits = paymentIntentJson.path("amount").asLong();
        JsonNode walletIdNode = paymentIntentJson.path("metadata").path("wallet_id");

        if (walletIdNode.isMissingNode() || walletIdNode.asText().isBlank()) {
            log.warn("payment_intent.succeeded for {} has no wallet_id metadata — cannot credit anything",
                    paymentIntentId);
            return;
        }

        Long walletId = walletIdNode.asLong();
        BigDecimal amount = BigDecimal.valueOf(amountMinorUnits, 2);

        // intent id is the idempotency key — LedgerService.topUp already no-ops on a
        // repeated referenceId, which covers Stripe's documented at-least-once
        // webhook delivery for free.
        ledgerService.topUp(walletId, amount, paymentIntentId);
        log.info("Credited wallet {} with {} from PaymentIntent {}", walletId, amount, paymentIntentId);
    }
}
