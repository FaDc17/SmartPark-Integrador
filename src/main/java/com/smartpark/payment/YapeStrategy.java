package com.smartpark.payment;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class YapeStrategy implements MetodoPagoStrategy {
    public String getNombre() { return "Yape"; }
    public void validar(BigDecimal total, BigDecimal montoRecibido) { }
}