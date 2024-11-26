package com.playtomic.tests.wallet.service;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class WalletEventsListener {

    private final WalletRepository walletRepository;
    private final WalletEntryRepository walletEntryRepository;

    /**
     * In a distributed systems context I will use a kafka like event streaming platform instead
     * For the sake of simplicity of the challenge I'm using local spring events
     */
    @EventListener
    public void handleWalletEntryCreatedEvent(WalletEntryCreatedEvent event) {
        Wallet wallet = walletRepository.findById(event.walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + event.walletId));
        List<WalletEntry> walletEntries = walletEntryRepository.findEventsAfter(event.walletId, wallet.getLastProcessedWalletEntry());
        if (walletEntries.isEmpty()) {
            return;
        }

        long newBalanceInMinor = calculateNewBalance(wallet.getBalanceInMinor(), walletEntries);

        wallet.updateBalance(newBalanceInMinor, getLastProcessedWalletEntry(walletEntries));
        walletRepository.save(wallet);
    }

    private static long getLastProcessedWalletEntry(List<WalletEntry> walletEntries) {
        return walletEntries
                .stream()
                .mapToLong(WalletEntry::getId)
                .max()
                .orElseThrow(() -> new IllegalStateException("No entries found"));
    }

    private long calculateNewBalance(long balance, List<WalletEntry> walletEntries) {

        return walletEntries
                .stream()
                .mapToLong(WalletEntry::getAmount)
                .reduce(balance, Long::sum);
    }
}
