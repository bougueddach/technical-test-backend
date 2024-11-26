package com.playtomic.tests.wallet.service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletEntryRepository extends JpaRepository<WalletEntry, Long> {
}
