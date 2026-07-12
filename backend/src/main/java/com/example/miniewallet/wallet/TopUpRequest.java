package com.example.miniewallet.wallet;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopUpRequest(
        @NotNull
        @DecimalMin(value = "10", message = "Top-up amount must be at least 10")
        @DecimalMax(value = "5000", message = "Top-up amount must be at most 5000")
        BigDecimal amount,

        @NotBlank
        String referenceId) {
}
