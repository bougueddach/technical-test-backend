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

    @EventListener
    public void handleWalletEntryCreatedEvent(WalletEntryCreatedEvent event) {
        Wallet wallet = walletRepository.findById(event.walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + event.walletId));
        List<WalletEntry> walletEntries = walletEntryRepository.findEventsAfter(event.walletId, wallet.getLastProcessedWalletEntry());

        long newBalanceInMinor = calculateNewBalance(wallet.getBalanceInMinor(), walletEntries);

        wallet.updateBalance(newBalanceInMinor, walletEntries.stream().mapToLong(WalletEntry::getId).max().getAsLong());
        walletRepository.save(wallet);
    }

    private long calculateNewBalance(long balance, List<WalletEntry> walletEntries) {

//        for (WalletEntry entry : walletEntries) {
//            balance += entry.getAmount();
//        }
//        return balance;
        return walletEntries.stream()
                .mapToLong(WalletEntry::getAmount)
                .reduce(balance, Long::sum);
    }
}
