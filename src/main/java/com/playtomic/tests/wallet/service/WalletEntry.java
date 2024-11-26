package com.playtomic.tests.wallet.service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.Instant;

@Entity
public class WalletEntry {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long walletId;
    private String paymentId;
    @Getter
    private long amount;
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
