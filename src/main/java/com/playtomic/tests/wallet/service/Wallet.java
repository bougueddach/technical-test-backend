package com.playtomic.tests.wallet.service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Getter
    private long balanceInMinor;
    @Getter
    private String currencyCode;
    @Getter
    private long lastProcessedWalletEntry;

    public Wallet() {}

    public Wallet(long balanceInMinor, String currencyCode) {
        this.balanceInMinor = balanceInMinor;
        this.currencyCode = currencyCode;
    }

    public void updateBalance(long amount, long lastProcessedWalletEntry) {
        this.balanceInMinor = amount;
        this.lastProcessedWalletEntry = lastProcessedWalletEntry;
    }
}
