package com.example.miniewallet.wallet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

/**
 * Sets the Stripe SDK's static API key once at startup. The SDK is
 * designed around this static field, not per-call configuration.
 */
@Component
public class StripeConfig {

    @Value("${app.stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
}
