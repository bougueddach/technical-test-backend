package com.playtomic.tests.wallet.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class WalletEntryRepositoryTest {

    public static final WalletEntry WALLET_ENTRY_1 = new WalletEntry(1L, "paymentId", 13L, Instant.now());
    public static final WalletEntry WALLET_ENTRY_2 = new WalletEntry(1L, "paymentId", 10L, Instant.now());
    public static final WalletEntry WALLET_ENTRY_3 = new WalletEntry(1L, "paymentId", 20L, Instant.now());
    @Autowired
    private WalletEntryRepository walletEntryRepository;

    @Test
    void findEventsAfter_shouldReturnCorrectEntries() {
        walletEntryRepository.save(WALLET_ENTRY_1);
        walletEntryRepository.save(WALLET_ENTRY_2);
        walletEntryRepository.save(WALLET_ENTRY_3);

        List<WalletEntry> result = walletEntryRepository.findEventsAfter(1L, 2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        assertThat(result.get(0).getAmount()).isEqualTo(20);
    }

    @Test
    void findEventsAfter_shouldReturnEmptyListIfNoEntriesMatch() {
        walletEntryRepository.save(WALLET_ENTRY_1);

        List<WalletEntry> result = walletEntryRepository.findEventsAfter(2L, 10L);

        assertThat(result).isEmpty();
    }
}