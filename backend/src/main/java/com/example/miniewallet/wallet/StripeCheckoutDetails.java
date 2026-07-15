package com.example.miniewallet.wallet;

import java.math.BigDecimal;

public record StripeCheckoutDetails(BigDecimal amount, String referenceId, Long walletId) {
}
