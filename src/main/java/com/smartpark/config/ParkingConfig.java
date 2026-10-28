package com.smartpark.config;

import java.math.BigDecimal;

public final class ParkingConfig {
    private final BigDecimal ratePerMinute = new BigDecimal("0.10");
    private final int capacity = 100;

    // Constructor privado: nadie puede hacer "new ParkingConfig()".
    private ParkingConfig() {
    }

    // Aquí vive la única instancia; Java la crea la primera vez que se usa.
    private static class Holder {
        private static final ParkingConfig INSTANCE = new ParkingConfig();
    }

    // Único punto de acceso a la instancia.
    public static ParkingConfig getInstance() {
        return Holder.INSTANCE;
    }

    public BigDecimal getRatePerMinute() {
        return ratePerMinute;
    }

    public int getCapacity() {
        return capacity;
    }
}