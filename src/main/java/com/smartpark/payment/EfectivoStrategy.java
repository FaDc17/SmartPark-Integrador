package com.smartpark.payment;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class EfectivoStrategy implements MetodoPagoStrategy {
    public String getNombre() { return "Efectivo"; }

    public void validar(BigDecimal total, BigDecimal montoRecibido) {
        if (montoRecibido == null || montoRecibido.compareTo(total) < 0) {
            throw new IllegalArgumentException("El monto recibido no cubre el total a pagar.");
        }
    }
}