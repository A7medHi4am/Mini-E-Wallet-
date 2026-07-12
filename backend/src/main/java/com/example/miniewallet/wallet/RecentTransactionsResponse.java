package com.example.miniewallet.wallet;

import java.util.List;

public record RecentTransactionsResponse(Long walletId, List<TransactionResponse> transactions) {
}
