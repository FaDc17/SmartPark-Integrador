package com.smartpark.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EfectivoStrategyTest {

    private final EfectivoStrategy strategy = new EfectivoStrategy();

    @Test
    void nombre() {
        assertEquals("Efectivo", strategy.getNombre());
    }

    @Test
    @DisplayName("Happy: monto igual al total")
    void exactAmount() {
        assertDoesNotThrow(() -> strategy.validar(new BigDecimal("10.00"), new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Happy: monto mayor al total")
    void greaterAmount() {
        assertDoesNotThrow(() -> strategy.validar(new BigDecimal("10.00"), new BigDecimal("20.00")));
    }

    @Test
    @DisplayName("Unhappy: monto nulo")
    void nullAmount() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> strategy.validar(new BigDecimal("10.00"), null));
        assertEquals("El monto recibido no cubre el total a pagar.", ex.getMessage());
    }

    @Test
    @DisplayName("Unhappy: monto menor al total")
    void lowerAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> strategy.validar(new BigDecimal("10.00"), new BigDecimal("9.99")));
    }
}
