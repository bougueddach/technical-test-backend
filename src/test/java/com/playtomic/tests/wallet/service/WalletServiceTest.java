package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.api.TopUpRequest;
import com.playtomic.tests.wallet.api.WalletDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    public static final long WALLET_ID = 1L;
    public static final String CREDIT_CARD_NUMBER = "0000 1111 2222 3333";
    public static final BigDecimal BIG_DECIMAL_OF_5 = BigDecimal.valueOf(5);
    public static final BigDecimal BIG_DECIMAL_OF_20 = BigDecimal.valueOf(20);
    public static final String PAYMENT_ID = "payment_id";
    public static final Instant AN_INSTANT = Instant.parse("2024-11-26T10:15:30Z");
    @Mock
    private WalletRepository repository;
    @Mock
    private WalletEntryRepository walletEntryRepository;
    @Mock
    private StripeService stripeService;
    @Mock
    private Clock clock;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<WalletEntry> walletEntryArgumentCaptor;
    @Captor
    private ArgumentCaptor<WalletUpdatedEvent> walletUpdatedEventCaptor;

    @InjectMocks
    private WalletService sut;


    @Test
    void getWallet_whenWalletDoesNotExist_shouldThrowException() {
        when(repository.findById(WALLET_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sut.getWallet(WALLET_ID));
    }

    @Test
    void getWallet_whenWalletExist_shouldReturnItFormatted() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Wallet(1010L, "EUR")));

        WalletDTO result = sut.getWallet(WALLET_ID);

        assertThat(result.balance()).isEqualTo("10.1 EUR");
    }

//    @Test
//    void topUp_whenWalletDoesNotExist_shouldThrowException() {
//        when(repository.findById(WALLET_ID)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, ()-> sut.topUp(WALLET_ID, new TopUpRequest(CREDIT_CARD_NUMBER, BigDecimal.valueOf(20))));
//    }

    @Test
    void topUp_whenAmountLessThen10_shouldThrowException() {
        when(stripeService.charge(CREDIT_CARD_NUMBER, BIG_DECIMAL_OF_5)).thenThrow(StripeAmountTooSmallException.class);

        assertThrows(StripeAmountTooSmallException.class, () -> sut.topUp(WALLET_ID, new TopUpRequest(CREDIT_CARD_NUMBER, BIG_DECIMAL_OF_5)));
    }

    @Test
    void topUp_whenAmountMoreThen10_shouldSaveWalletEntry() {

        when(stripeService.charge(CREDIT_CARD_NUMBER, BIG_DECIMAL_OF_20)).thenReturn(new Payment(PAYMENT_ID));
        when(clock.instant()).thenReturn(AN_INSTANT);

        sut.topUp(WALLET_ID, new TopUpRequest(CREDIT_CARD_NUMBER, BIG_DECIMAL_OF_20));

        verify(walletEntryRepository).save(walletEntryArgumentCaptor.capture());
        WalletEntry capturedWalletEntry = walletEntryArgumentCaptor.getValue();
        assertThat(capturedWalletEntry).extracting("walletId").isEqualTo(WALLET_ID);
        assertThat(capturedWalletEntry).extracting("paymentId").isEqualTo(PAYMENT_ID);
        assertThat(capturedWalletEntry).extracting("amount").isEqualTo(BIG_DECIMAL_OF_20.longValue());
        assertThat(capturedWalletEntry).extracting("creation_time").isEqualTo(AN_INSTANT);

        verify(eventPublisher).publishEvent(walletUpdatedEventCaptor.capture());
        WalletUpdatedEvent capturedEvent = walletUpdatedEventCaptor.getValue();

        assertThat(capturedEvent.walletId).isEqualTo(WALLET_ID);
        assertThat(capturedEvent.amount).isEqualTo(BIG_DECIMAL_OF_20.toBigInteger().longValue());
    }
}