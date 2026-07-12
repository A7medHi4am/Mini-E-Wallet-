package com.example.miniewallet.merchant;

import jakarta.validation.constraints.NotNull;

public record MerchantStatusRequest(@NotNull Boolean active) {
}
