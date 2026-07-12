package com.example.miniewallet.merchant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniewallet.common.domain.Merchant;
import com.example.miniewallet.common.exception.WalletNotFoundException;
import com.example.miniewallet.common.wallet.LedgerService;
import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.wallet.TransactionType;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;

@Service
public class PaymentService {

    private final MerchantService merchantService;
    private final WalletRepository wallets;
    private final LedgerService ledgerService;

    public PaymentService(MerchantService merchantService, WalletRepository wallets, LedgerService ledgerService) {
        this.merchantService = merchantService;
        this.wallets = wallets;
        this.ledgerService = ledgerService;
    }

    @Transactional
    public Transaction pay(Long payerUserId, PaymentRequest request) {
        Merchant merchant = merchantService.getMerchant(request.merchantId());
        if (!merchant.isActive()) {
            throw new MerchantInactiveException(merchant.getId());
        }

        Wallet payerWallet = wallets.findByUserId(payerUserId)
                .orElseThrow(() -> new WalletNotFoundException(payerUserId));
        Wallet merchantWallet = wallets.findByMerchantId(merchant.getId())
                .orElseThrow(() -> WalletNotFoundException.forWalletId(merchant.getId()));

        return ledgerService.move(
                payerWallet.getId(), merchantWallet.getId(), request.amount(),
                TransactionType.PAYMENT, request.referenceId());
    }
}
