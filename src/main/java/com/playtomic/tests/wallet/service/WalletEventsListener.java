package com.playtomic.tests.wallet.service;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@AllArgsConstructor
public class WalletEventsListener {

    private final WalletRepository walletRepository;
    private final WalletEntryRepository walletEntryRepository;
    private final ConcurrentHashMap<Long, ReentrantLock> walletLocks = new ConcurrentHashMap<>();

    /**
     * 1- In a distributed systems context I will use a kafka like event streaming platform instead
     *    For the sake of simplicity of the challenge I'm using local spring events
     * 2- We could use Syncronised here instead but using locks gives a better visibility about how it'll work in a
     *    distributed system by saving the locks in a distributed lock, with redis for example
     */
    @EventListener
    public void handleWalletEntryCreatedEvent(WalletEntryCreatedEvent event) {
        ReentrantLock lock = walletLocks.computeIfAbsent(event.walletId, id -> new ReentrantLock());
        lock.lock();
        try {
            snapshotWallet(event);
        } finally {
            lock.unlock();
            walletLocks.remove(event.walletId);
        }
    }

    private void snapshotWallet(WalletEntryCreatedEvent event) {
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
