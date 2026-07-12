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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.exception.InsufficientFundsException;
import com.example.miniewallet.common.exception.WalletFrozenException;

/**
 * Shared entity: the balance-mutation zone. Balance has NO public setter —
 * only LedgerServiceImpl (same package) is allowed to change it.
 */
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", unique = true)
    private User user;


    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected Wallet() {
        
    }

    public Wallet(User user) {
        this.user = user;
        this.balance = BigDecimal.ZERO;
        this.currency = "EGP";
        this.status = WalletStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    void credit(BigDecimal amount) {
        requireActive();
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    void debit(BigDecimal amount) {
        requireActive();
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(this.id, amount, this.balance);
        }
        this.balance = this.balance.subtract(amount);
    }

    private void requireActive() {
        if (this.status != WalletStatus.ACTIVE) {
            throw new WalletFrozenException(this.id);
        }
    }
}
