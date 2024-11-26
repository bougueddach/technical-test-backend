package com.playtomic.tests.wallet.service;

import com.playtomic.tests.wallet.helpers.CurrencyFormatter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyFormatterTest {

    @Test
    void majorToMinor() {
        assertThat(CurrencyFormatter.majorToMinor(BigDecimal.valueOf(10.5), "EUR")).isEqualTo(1050);
        assertThat(CurrencyFormatter.majorToMinor(BigDecimal.valueOf(10.25), "USD")).isEqualTo(1025);
        assertThat(CurrencyFormatter.majorToMinor(BigDecimal.valueOf(10.25), "JPY")).isEqualTo(10);
    }
}