package com.playtomic.tests.wallet.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;


public class CurrencyFormatter {

    /**
     * Converts a major value in major currency to minor
     */
    public static long majorToMinor(BigDecimal majorAmount, String currencyCode) {
        if (majorAmount == null || currencyCode == null) {
            throw new IllegalArgumentException("Major value and currency code cannot be null");
        }

        Currency currency = Currency.getInstance(currencyCode);
        int scale = currency.getDefaultFractionDigits();

        return majorAmount
                .multiply(BigDecimal.valueOf(Math.pow(10, scale)))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    /**
     * Converts a major value in minor currency
     */
    public static BigDecimal minorToMajor(long minorAMount, String currencyCode) {
        if (currencyCode == null) {
            throw new IllegalArgumentException("Currency code cannot be null");
        }

        Currency currency = Currency.getInstance(currencyCode);
        int scale = currency.getDefaultFractionDigits();

        return BigDecimal.valueOf(minorAMount)
                .divide(BigDecimal.valueOf(Math.pow(10, scale)), scale, RoundingMode.HALF_UP);
    }
}
