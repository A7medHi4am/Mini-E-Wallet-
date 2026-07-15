package com.example.miniewallet.wallet;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class StripeServiceTest {

    @Test
    void convertsAmountsToStripeCents() {
        StripeService service = new StripeService("", "http://localhost:5173/topup?checkout=success", "http://localhost:5173/topup?checkout=cancel");

        assertEquals(12550L, service.amountToCents(new BigDecimal("125.50")));
        assertEquals(1000L, service.amountToCents(new BigDecimal("10.00")));
    }
}
