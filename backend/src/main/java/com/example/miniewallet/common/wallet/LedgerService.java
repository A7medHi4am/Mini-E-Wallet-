package com.example.miniewallet.common.wallet;

import java.math.BigDecimal;

/**
 * Shared interface: the only public entry point for balance-changing
 * operations (credit / transfer). Persons 2, 3, and 5 depend on this.
 */
public interface LedgerService {

    Transaction topUp(Long walletId, BigDecimal amount, String referenceId);

    Transaction move(Long senderWalletId, Long receiverWalletId,
                      BigDecimal amount, TransactionType type, String referenceId);
}
