package com.example.miniewallet.common.wallet;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Shared entity: append-only audit row, written only by LedgerServiceImpl
 * in the same transaction as the balance change it records.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id")
    private Wallet senderWallet;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id", nullable = false)
    private Wallet receiverWallet;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "reference_id", nullable = false, unique = true)
    private String referenceId;

    @Column(nullable = false)
    private Instant createdAt;

    protected Transaction() {
    }

    public Transaction(TransactionType type, Wallet senderWallet, Wallet receiverWallet,
                        BigDecimal amount, String referenceId) {
        this.type = type;
        this.senderWallet = senderWallet;
        this.receiverWallet = receiverWallet;
        this.amount = amount;
        this.referenceId = referenceId;
        this.status = TransactionStatus.SUCCESS;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public Wallet getSenderWallet() {
        return senderWallet;
    }

    public Wallet getReceiverWallet() {
        return receiverWallet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
