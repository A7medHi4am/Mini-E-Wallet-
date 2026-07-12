package com.example.miniewallet.merchant;

import jakarta.validation.constraints.NotBlank;

public record MerchantRequest(
        @NotBlank String name,
        @NotBlank String category) {
}
