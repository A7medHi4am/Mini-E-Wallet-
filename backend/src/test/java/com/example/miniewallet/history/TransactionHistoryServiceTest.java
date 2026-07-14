package com.example.miniewallet.history;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.example.miniewallet.common.repository.TransactionReadRepository;
import com.example.miniewallet.common.wallet.TransactionType;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {

    @Mock
    private TransactionReadRepository transactions;

    @InjectMocks
    private TransactionHistoryService service;

    @Test
    void usesRepositoryWithoutDateBoundsWhenNoDatesAreProvided() {
        PageRequest pageable = PageRequest.of(0, 15);

        service.getHistory(42L, TransactionType.TOPUP, null, null, pageable);

        verify(transactions).findForWallet(42L, TransactionType.TOPUP, pageable);
        verifyNoMoreInteractions(transactions);
    }

    @Test
    void usesRepositoryWithFromDateWhenOnlyStartDateIsProvided() {
        PageRequest pageable = PageRequest.of(0, 15);
        LocalDate from = LocalDate.of(2026, 7, 1);

        service.getHistory(42L, TransactionType.TOPUP, from, null, pageable);

        verify(transactions).findForWalletFrom(42L, TransactionType.TOPUP, from.atStartOfDay().toInstant(java.time.ZoneOffset.UTC), pageable);
        verifyNoMoreInteractions(transactions);
    }
}
