package com.playtomic.tests.wallet.service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(WalletRepository walletRepository) {
        return args -> {
            walletRepository.save(new Wallet(1000L, "USD"));
            walletRepository.save(new Wallet(4444L, "USD"));
            walletRepository.save(new Wallet(5000L, "EUR"));
            walletRepository.save(new Wallet(9000L, "EUR"));
            walletRepository.save(new Wallet(9999L, "EUR"));
        };
    }
}