package com.example.miniewallet.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miniewallet.common.domain.Merchant;

/**
 * Shared repository for Merchant.
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Page<Merchant> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Merchant> findByActiveTrue(Pageable pageable);

    Page<Merchant> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
