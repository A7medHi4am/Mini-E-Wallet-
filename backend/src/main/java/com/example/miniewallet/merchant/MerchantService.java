package com.example.miniewallet.merchant;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniewallet.admin.AdminAuditService;
import com.example.miniewallet.common.domain.Merchant;
import com.example.miniewallet.common.exception.WalletNotFoundException;
import com.example.miniewallet.common.repository.MerchantRepository;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;

@Service
public class MerchantService {

    private final MerchantRepository merchants;
    private final WalletRepository wallets;
    private final AdminAuditService auditService;

    public MerchantService(MerchantRepository merchants, WalletRepository wallets, AdminAuditService auditService) {
        this.merchants = merchants;
        this.wallets = wallets;
        this.auditService = auditService;
    }

    @Transactional
    public Merchant createMerchant(MerchantRequest request) {
        Merchant merchant = new Merchant(request.name(), request.category());
        merchants.save(merchant);

        Wallet wallet = new Wallet(merchant);
        wallets.save(wallet);

        auditService.logAction("CREATE_MERCHANT", "MERCHANT", merchant.getId());
        return merchant;
    }

    public Merchant getMerchant(Long id) {
        return merchants.findById(id).orElseThrow(() -> new MerchantNotFoundException(id));
    }

    public Wallet getWalletFor(Merchant merchant) {
        return wallets.findByMerchantId(merchant.getId())
                .orElseThrow(() -> WalletNotFoundException.forWalletId(merchant.getId()));
    }

    public Page<Merchant> listAll(Pageable pageable) {
        return merchants.findAll(pageable);
    }

    public Page<Merchant> search(String query, boolean activeOnly, Pageable pageable) {
        String q = query == null ? "" : query.trim();
        if (activeOnly) {
            return merchants.findByActiveTrueAndNameContainingIgnoreCase(q, pageable);
        }
        return merchants.findByNameContainingIgnoreCase(q, pageable);
    }

    @Transactional
    public Merchant updateMerchant(Long id, MerchantRequest request) {
        Merchant merchant = getMerchant(id);
        merchant.rename(request.name(), request.category());
        auditService.logAction("UPDATE_MERCHANT", "MERCHANT", merchant.getId());
        return merchant;
    }

    @Transactional
    public Merchant setActive(Long id, boolean active) {
        Merchant merchant = getMerchant(id);
        merchant.setActive(active);
        auditService.logAction(active ? "ACTIVATE_MERCHANT" : "DEACTIVATE_MERCHANT", "MERCHANT", merchant.getId());
        return merchant;
    }

    @Transactional
    public void deleteMerchant(Long id) {
        Merchant merchant = getMerchant(id);
        Wallet wallet = getWalletFor(merchant);
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new MerchantHasBalanceException(id);
        }
        wallets.delete(wallet);
        merchants.delete(merchant);
        auditService.logAction("DELETE_MERCHANT", "MERCHANT", id);
    }
}
