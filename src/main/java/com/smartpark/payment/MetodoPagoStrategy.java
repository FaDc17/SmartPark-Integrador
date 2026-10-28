package com.smartpark.payment;

import java.math.BigDecimal;

public interface MetodoPagoStrategy {
    String getNombre();
    void validar(BigDecimal total, BigDecimal montoRecibido);
}