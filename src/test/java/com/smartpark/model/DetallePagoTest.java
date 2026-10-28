package com.smartpark.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DetallePagoTest {

    @Test
    @DisplayName("Happy: valores iniciales")
    void defaults() {
        DetallePago d = new DetallePago();
        assertNull(d.getId());
        assertNull(d.getPago());
        assertNull(d.getCliente());
        assertNull(d.getPlaca());
        assertNull(d.getHoraIngreso());
        assertNull(d.getHoraSalida());
        assertEquals(0, d.getMinutosEstacionado());
        assertNull(d.getTotal());
    }

    @Test
    @DisplayName("Happy: setters y getters")
    void settersAndGetters() {
        DetallePago d = new DetallePago();
        Pago pago = new Pago();
        LocalDateTime in = LocalDateTime.of(2026, 1, 1, 8, 0);
        LocalDateTime out = in.plusHours(2);
        d.setPago(pago);
        d.setCliente("Ana");
        d.setPlaca("ABC-123");
        d.setHoraIngreso(in);
        d.setHoraSalida(out);
        d.setMinutosEstacionado(120);
        d.setTotal(new BigDecimal("12.00"));
        assertSame(pago, d.getPago());
        assertEquals("Ana", d.getCliente());
        assertEquals("ABC-123", d.getPlaca());
        assertEquals(in, d.getHoraIngreso());
        assertEquals(out, d.getHoraSalida());
        assertEquals(120, d.getMinutosEstacionado());
        assertEquals(new BigDecimal("12.00"), d.getTotal());
    }
}
