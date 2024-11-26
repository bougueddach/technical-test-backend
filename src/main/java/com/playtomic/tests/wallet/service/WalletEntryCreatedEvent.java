package com.playtomic.tests.wallet.service;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;

public class WalletEntryCreatedEvent extends ApplicationEvent {
    public long walletId;
    public long amount;

    public WalletEntryCreatedEvent(long walletId, long amount, Instant timestamp) {
        super(timestamp);
        this.walletId = walletId;
        this.amount = amount;
    }
}
