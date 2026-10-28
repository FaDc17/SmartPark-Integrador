package com.smartpark.payment;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TarjetaStrategy implements MetodoPagoStrategy {
    public String getNombre() { return "Tarjeta"; }
    public void validar(BigDecimal total, BigDecimal montoRecibido) { }
}