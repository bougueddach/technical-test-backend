package com.playtomic.tests.wallet.api;

import java.math.BigDecimal;

public record TopUpRequest(String creditCardNumber, BigDecimal amount) {
}
