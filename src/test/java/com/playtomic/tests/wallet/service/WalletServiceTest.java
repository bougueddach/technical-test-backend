package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.api.WalletDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository repository;

    @InjectMocks
    private WalletService sut;


    @Test
    void getWallet_whenWalletDoesNotExist_shouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()-> sut.getWallet(1));
    }

    @Test
    void getWallet_whenWalletExist_shouldReturnItFormatted() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Wallet(1010L, "EUR")));

        WalletDTO result = sut.getWallet(1);

        assertThat(result.balance()).isEqualTo("10.1 EUR");
    }
}