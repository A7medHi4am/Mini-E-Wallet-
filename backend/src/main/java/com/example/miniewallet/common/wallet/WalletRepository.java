package com.example.miniewallet.common.wallet;

/**
 * Shared repository: findByIdForUpdate should use @Lock(PESSIMISTIC_WRITE).
 * Only LedgerServiceImpl should depend on this.
 */
public interface WalletRepository {
}
