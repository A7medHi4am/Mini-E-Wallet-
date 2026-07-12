package com.example.miniewallet.common.wallet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByReferenceId(String referenceId);

    List<Transaction> findBySenderWalletIdOrReceiverWalletIdOrderByCreatedAtDesc(
            Long senderWalletId, Long receiverWalletId, Pageable pageable);
}
