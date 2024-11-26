package com.playtomic.tests.wallet.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletEntryRepository extends JpaRepository<WalletEntry, Long> {

    @Query("SELECT WE from WalletEntry WE WHERE WE.walletId = :walletId AND WE.id > :lastProcessedWalletEntry")
    List<WalletEntry> findEventsAfter(long walletId, long lastProcessedWalletEntry);
}
