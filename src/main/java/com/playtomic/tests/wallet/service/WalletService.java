package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.api.WalletDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    public WalletDTO getWallet(long id) {
        // I thought of returning the wallet's info formatted might be better (no over engineering intended)
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + id));

        return new WalletDTO(formatBalance(wallet.getBalanceInMinor(), wallet.getCurrencyCode()));
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
