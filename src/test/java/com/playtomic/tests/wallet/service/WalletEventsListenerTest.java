package com.playtomic.tests.wallet.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletEventsListenerTest {

    public static final Wallet A_WALLET = new Wallet(1000L, "USD");
    public static final long WALLET_ID = 1L;
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletEntryRepository walletEntryRepository;

    @InjectMocks
    private WalletEventsListener sut;

    @Captor
    private ArgumentCaptor<Wallet> walletArgumentCaptor;

    @Test
    void handleWalletEntryCreatedEvent_walletDoesNotExist_shouldThrowException() {
        WalletEntryCreatedEvent event = new WalletEntryCreatedEvent(1L, 1, Instant.now());
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sut.handleWalletEntryCreatedEvent(event));
    }

    @Test
    void handleWalletEntryCreatedEvent_whenWeFindUnprocessedEntries_shouldUpdateBalance() {
        WalletEntryCreatedEvent event = new WalletEntryCreatedEvent(1L, 1, Instant.now());
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(A_WALLET));
        WalletEntry entry1 = new WalletEntry(1L, "paymentId", 100L, Instant.now());
        ReflectionTestUtils.setField(entry1, "id", 1L);
        WalletEntry entry2 = new WalletEntry(2L, "paymentId", 200L, Instant.now());
        ReflectionTestUtils.setField(entry2, "id", 2L);
        when(walletEntryRepository.findEventsAfter(WALLET_ID, 0))
                .thenReturn(List.of(entry1, entry2));

        sut.handleWalletEntryCreatedEvent(event);

        verify(walletRepository).save(walletArgumentCaptor.capture());
        Wallet capturedWallet = walletArgumentCaptor.getValue();

        assertThat(capturedWallet.getBalanceInMinor()).isEqualTo(1300L);
    }

    @Test
    void handleWalletEntryCreatedEvent_whenNoUnprocessedEntryExist_shouldDoNothing() {
        WalletEntryCreatedEvent event = new WalletEntryCreatedEvent(1L, 1, Instant.now());
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(A_WALLET));
        when(walletEntryRepository.findEventsAfter(WALLET_ID, 0))
                .thenReturn(Collections.emptyList());

        sut.handleWalletEntryCreatedEvent(event);

        verify(walletRepository, times(0)).save(walletArgumentCaptor.capture());
        verifyNoMoreInteractions(walletRepository);
    }
}