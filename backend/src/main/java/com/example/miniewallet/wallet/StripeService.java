package com.example.miniewallet.wallet;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

    private final String secretKey;
    private final String successUrl;
    private final String cancelUrl;

    public StripeService(@Value("${app.stripe.secret-key:}") String secretKey,
                         @Value("${app.stripe.success-url:http://localhost:5173/topup?checkout=success}") String successUrl,
                         @Value("${app.stripe.cancel-url:http://localhost:5173/topup?checkout=cancel}") String cancelUrl) {
        this.secretKey = secretKey;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        if (StringUtils.hasText(secretKey)) {
            Stripe.apiKey = secretKey;
        }
    }

    public TopUpCheckoutResponse createCheckoutSession(BigDecimal amount, String referenceId, Long walletId) {
        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalStateException("Stripe secret key is not configured");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(appendSessionPlaceholder(successUrl))
                .setCancelUrl(cancelUrl)
                .putMetadata("referenceId", referenceId)
                .putMetadata("walletId", String.valueOf(walletId))
                .putMetadata("amount", amount.setScale(2, RoundingMode.HALF_UP).toPlainString())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("egp")
                                .setUnitAmount(amountToCents(amount))
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Wallet top-up")
                                        .build())
                                .build())
                        .build())
                .build();

        try {
            Session session = Session.create(params);
            return new TopUpCheckoutResponse(session.getUrl(), session.getId());
        } catch (StripeException ex) {
            throw new IllegalStateException("Unable to create Stripe checkout session", ex);
        }
    }

    public Session retrieveSession(String sessionId) {
        try {
            return Session.retrieve(sessionId);
        } catch (StripeException ex) {
            throw new IllegalStateException("Unable to retrieve Stripe checkout session", ex);
        }
    }

    public boolean isPaid(String sessionId) {
        Session session = retrieveSession(sessionId);
        return "paid".equals(session.getPaymentStatus());
    }

    public StripeCheckoutDetails getCheckoutDetails(String sessionId) {
        Session session = retrieveSession(sessionId);
        String amountValue = session.getMetadata().get("amount");
        String referenceId = session.getMetadata().get("referenceId");
        String walletIdValue = session.getMetadata().get("walletId");
        BigDecimal amount = new BigDecimal(amountValue);
        Long walletId = walletIdValue == null ? null : Long.valueOf(walletIdValue);
        return new StripeCheckoutDetails(amount, referenceId, walletId);
    }

    long amountToCents(BigDecimal amount) {
        return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private String appendSessionPlaceholder(String url) {
        return url.contains("?") ? url + "&session_id={CHECKOUT_SESSION_ID}" : url + "?session_id={CHECKOUT_SESSION_ID}";
    }
}
