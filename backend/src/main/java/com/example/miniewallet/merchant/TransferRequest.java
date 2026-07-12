package com.example.miniewallet.merchant;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransferRequest(
        @NotBlank(message = "Recipient email or phone is required")
        String recipient,

        @NotNull
        @DecimalMin(value = "0.01", message = "Transfer amount must be positive")
        BigDecimal amount,

        @NotBlank
        String referenceId) {
}
