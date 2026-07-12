package com.example.miniewallet.merchant;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull
        Long merchantId,

        @NotNull
        @DecimalMin(value = "0.01", message = "Payment amount must be positive")
        BigDecimal amount,

        @NotBlank
        String referenceId) {
}
