package com.example.miniewallet.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.repository.UserRepository;
import com.example.miniewallet.common.wallet.TransactionRepository;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;

@Service
public class AdminService {

    private final UserRepository users;
    private final WalletRepository wallets;
    private final TransactionRepository transactions;
    private final AdminAuditService auditService;

    public AdminService(UserRepository users,
                        WalletRepository wallets,
                        TransactionRepository transactions,
                        AdminAuditService auditService) {
        this.users = users;
        this.wallets = wallets;
        this.transactions = transactions;
        this.auditService = auditService;
    }

    public Page<UserAdminResponse> listUsers(Pageable pageable) {
        Page<User> page = users.findAll(pageable);
        auditService.logAction("VIEW_USERS", "USER", null);
        return page.map(UserAdminResponse::from);
    }

    public Page<WalletAdminResponse> listWallets(Pageable pageable) {
        Page<Wallet> page = wallets.findAll(pageable);
        auditService.logAction("VIEW_WALLETS", "WALLET", null);
        return page.map(WalletAdminResponse::from);
    }

    public Page<TransactionAdminResponse> listTransactions(Pageable pageable) {
        Page<com.example.miniewallet.common.wallet.Transaction> page = transactions.findAll(pageable);
        auditService.logAction("VIEW_TRANSACTIONS", "TRANSACTION", null);
        return page.map(TransactionAdminResponse::from);
    }

    @Transactional
    public WalletAdminResponse freezeWallet(Long walletId) {
        Wallet wallet = wallets.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));
        wallet.freeze();
        wallets.save(wallet);
        auditService.logAction("FREEZE_WALLET", "WALLET", walletId);
        return WalletAdminResponse.from(wallet);
    }

    @Transactional
    public WalletAdminResponse unfreezeWallet(Long walletId) {
        Wallet wallet = wallets.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));
        wallet.activate();
        wallets.save(wallet);
        auditService.logAction("UNFREEZE_WALLET", "WALLET", walletId);
        return WalletAdminResponse.from(wallet);
    }
}
