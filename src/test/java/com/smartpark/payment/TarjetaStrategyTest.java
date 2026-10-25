package com.smartpark.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaStrategyTest {

    private final TarjetaStrategy strategy = new TarjetaStrategy();

    @Test
    void nombre() {
        assertEquals("Tarjeta", strategy.getNombre());
    }

    @Test
    @DisplayName("Happy: no valida montos (acepta nulo)")
    void validarAceptaCualquierMonto() {
        assertDoesNotThrow(() -> strategy.validar(new BigDecimal("10"), null));
        assertDoesNotThrow(() -> strategy.validar(new BigDecimal("10"), BigDecimal.ZERO));
    }
}
