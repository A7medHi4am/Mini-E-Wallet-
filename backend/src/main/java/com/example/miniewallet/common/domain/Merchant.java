package com.example.miniewallet.common.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Shared entity: merchant. Owns a Wallet, same 1:1 pattern as User
 * (the Wallet holds the foreign key, Merchant does not reference it back).
 */
@Entity
@Table(name = "merchants")
public class Merchant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Instant createdAt;

    protected Merchant() {
    }

    public Merchant(String name, String category) {
        this.name = name;
        this.category = category;
        this.active = true;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void rename(String name, String category) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (category != null && !category.isBlank()) {
            this.category = category;
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
