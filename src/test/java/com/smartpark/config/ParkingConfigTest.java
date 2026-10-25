package com.smartpark.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ParkingConfigTest {

    @Test
    @DisplayName("Happy: Singleton devuelve siempre la misma instancia")
    void singleton() {
        assertSame(ParkingConfig.getInstance(), ParkingConfig.getInstance());
    }

    @Test
    @DisplayName("Happy: tarifa y capacidad configuradas")
    void values() {
        ParkingConfig c = ParkingConfig.getInstance();
        assertEquals(new BigDecimal("0.10"), c.getRatePerMinute());
        assertEquals(100, c.getCapacity());
    }

    @Test
    @DisplayName("Unhappy: el constructor es privado (no se puede instanciar desde fuera)")
    void privateConstructor() throws Exception {
        Constructor<ParkingConfig> ctor = ParkingConfig.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);
        ParkingConfig other = ctor.newInstance();
        assertNotSame(ParkingConfig.getInstance(), other);
    }
}
