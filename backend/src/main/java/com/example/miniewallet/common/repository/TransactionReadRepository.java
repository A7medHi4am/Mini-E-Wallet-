package com.example.miniewallet.common.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.wallet.TransactionType;

/**
 * Shared, read-only repository for Transaction — no save/delete.
 * Used by history and admin packages.
 */
public interface TransactionReadRepository extends Repository<Transaction, Long> {

    @Query(value = "select t from Transaction t "
            + "left join fetch t.senderWallet sw "
            + "left join fetch sw.user "
            + "left join fetch sw.merchant "
            + "left join fetch t.receiverWallet rw "
            + "left join fetch rw.user "
            + "left join fetch rw.merchant "
            + "where (sw.id = :walletId or rw.id = :walletId) "
            + "and (:type is null or t.type = :type) "
            + "and (:from is null or t.createdAt >= :from) "
            + "and (:to is null or t.createdAt < :to) "
            + "order by t.createdAt desc",
            countQuery = "select count(t) from Transaction t "
            + "where (t.senderWallet.id = :walletId or t.receiverWallet.id = :walletId) "
            + "and (:type is null or t.type = :type) "
            + "and (:from is null or t.createdAt >= :from) "
            + "and (:to is null or t.createdAt < :to)")
    Page<Transaction> findForWallet(@Param("walletId") Long walletId,
                                     @Param("type") TransactionType type,
                                     @Param("from") Instant from,
                                     @Param("to") Instant to,
                                     Pageable pageable);
}
