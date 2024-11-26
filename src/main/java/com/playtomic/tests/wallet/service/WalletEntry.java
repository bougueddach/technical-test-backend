package com.playtomic.tests.wallet.service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class WalletEntry {

    @Id
    private Long id;
    private Long walletId;
    private String paymentId;
    private Long amount;
    private Instant creation_time;
    private String currencyCode;

    public WalletEntry() {

    }

    public WalletEntry(long walletId, String paymentId, long amount, Instant now) {
        this.walletId = walletId;
        this.amount = amount;
        this.paymentId = paymentId;
        this.creation_time = now;
    }
}
