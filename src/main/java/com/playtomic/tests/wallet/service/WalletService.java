package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.api.TopUpRequest;
import com.playtomic.tests.wallet.api.WalletDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletEntryRepository walletEntryRepository;
    @Autowired
    private StripeService stripeService;
    @Autowired
    private Clock clock;
    @Autowired
    private ApplicationEventPublisher eventPublisher;


    public WalletDTO getWallet(long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + id));

        // I thought of returning the wallet's info formatted might be better (no over engineering intended)
        return new WalletDTO(formatBalance(wallet.getBalanceInMinor(), wallet.getCurrencyCode()));
    }

    public void topUp(long walletId, TopUpRequest requestBody) {
        // TODO throw an exception if wallet doesn't exist
        Payment payment = stripeService.charge(requestBody.creditCardNumber(), requestBody.amount());
        Instant topUpTime = clock.instant();
        WalletEntry walletEntry = new WalletEntry(walletId, payment.getId(), requestBody.amount().longValue(), topUpTime);
        walletEntryRepository.save(walletEntry);
        eventPublisher.publishEvent(new WalletEntryCreatedEvent(walletId, requestBody.amount().longValue(), topUpTime));
    }

    /**
     * Very basic minor to major conversion supporting EUR and USD, could replace in future
     *
     * @param balanceInMinor
     * @param currencyCode
     * @return a formatted string with amount in major and currency
     */
    private String formatBalance(long balanceInMinor, String currencyCode) {
        float amountInMajor = balanceInMinor / 100f;

        return amountInMajor + " " + currencyCode;
    }
}
