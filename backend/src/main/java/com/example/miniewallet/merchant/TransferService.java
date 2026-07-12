package com.example.miniewallet.merchant;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.exception.WalletNotFoundException;
import com.example.miniewallet.common.repository.UserRepository;
import com.example.miniewallet.common.wallet.LedgerService;
import com.example.miniewallet.common.wallet.Transaction;
import com.example.miniewallet.common.wallet.TransactionType;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;

@Service
public class TransferService {

    private final UserRepository users;
    private final WalletRepository wallets;
    private final LedgerService ledgerService;

    public TransferService(UserRepository users, WalletRepository wallets, LedgerService ledgerService) {
        this.users = users;
        this.wallets = wallets;
        this.ledgerService = ledgerService;
    }

    @Transactional
    public Transaction transfer(Long senderUserId, TransferRequest request) {
        User recipient = resolveRecipient(request.recipient());
        if (recipient.getId().equals(senderUserId)) {
            throw new SelfTransferException();
        }

        Wallet senderWallet = wallets.findByUserId(senderUserId)
                .orElseThrow(() -> new WalletNotFoundException(senderUserId));
        Wallet receiverWallet = wallets.findByUserId(recipient.getId())
                .orElseThrow(() -> new WalletNotFoundException(recipient.getId()));

        return ledgerService.move(
                senderWallet.getId(), receiverWallet.getId(), request.amount(),
                TransactionType.TRANSFER, request.referenceId());
    }

    private User resolveRecipient(String identifier) {
        Optional<User> byEmail = users.findByEmail(identifier);
        if (byEmail.isPresent()) {
            return byEmail.get();
        }
        return users.findByPhone(identifier)
                .orElseThrow(() -> new RecipientNotFoundException(identifier));
    }
}
