package com.playtomic.tests.wallet.service;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;

public class WalletUpdatedEvent extends ApplicationEvent {
    public long walletId;
    public long amount;

    public WalletUpdatedEvent(long walletId, long amount, Instant timestamp) {
        super(timestamp);
        this.walletId = walletId;
        this.amount = amount;
    }
}
