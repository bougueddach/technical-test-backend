package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.api.TopUpRequest;
import com.playtomic.tests.wallet.api.WalletDTO;
import com.playtomic.tests.wallet.helpers.CurrencyFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Currency;

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

        // I thought returning the balance formatted might be better
        // (no over engineering intended, sorry if I should have returned the plain wallet entity😅)
        return new WalletDTO(formatBalance(wallet.getBalanceInMinor(), wallet.getCurrencyCode()));
    }

    /**
     * Since the stripe api takes a BigDecimal I will assume that's the major and design my API to take that
     *
     */
    public void topUp(long walletId, TopUpRequest requestBody) {
        // TODO Throw an exception if wallet doesn't exist
        // TODO Check if currency is same as the wallet's currency

        Payment payment = stripeService.charge(requestBody.creditCardNumber(), requestBody.amount());

        Instant topUpTime = clock.instant();
        long amountInMinor = CurrencyFormatter.majorToMinor(requestBody.amount(), requestBody.currencyCode());
        WalletEntry walletEntry = new WalletEntry(walletId, payment.getId(), amountInMinor, topUpTime);

        walletEntryRepository.save(walletEntry);
        eventPublisher.publishEvent(new WalletEntryCreatedEvent(walletId, amountInMinor, topUpTime));
    }

    private String formatBalance(long balanceInMinor, String currencyCode) {
        return CurrencyFormatter.minorToMajor(balanceInMinor, currencyCode) + " " + Currency.getInstance(currencyCode).getSymbol();
    }
}
