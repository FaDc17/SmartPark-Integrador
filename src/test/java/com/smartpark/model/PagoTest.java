package com.smartpark.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PagoTest {

    @Test
    @DisplayName("Happy: estado por defecto es PAGADO y el id es nulo")
    void defaults() {
        Pago p = new Pago();
        assertEquals("PAGADO", p.getEstado());
        assertNull(p.getId());
        assertNull(p.getCodigoPago());
        assertNull(p.getVehicle());
        assertNull(p.getMonto());
        assertNull(p.getMetodoPago());
        assertNull(p.getFechaPago());
    }

    @Test
    @DisplayName("Happy: setters y getters")
    void settersAndGetters() {
        Pago p = new Pago();
        Vehicle v = new Vehicle("A", "P", 1);
        LocalDateTime now = LocalDateTime.now();
        p.setCodigoPago("SP-2026-000001");
        p.setVehicle(v);
        p.setMonto(new BigDecimal("5.00"));
        p.setMetodoPago("Yape");
        p.setFechaPago(now);
        p.setEstado("ANULADO");
        assertEquals("SP-2026-000001", p.getCodigoPago());
        assertSame(v, p.getVehicle());
        assertEquals(new BigDecimal("5.00"), p.getMonto());
        assertEquals("Yape", p.getMetodoPago());
        assertEquals(now, p.getFechaPago());
        assertEquals("ANULADO", p.getEstado());
    }
}
