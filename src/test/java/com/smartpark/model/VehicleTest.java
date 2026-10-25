package com.smartpark.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    @DisplayName("Happy: constructor con parámetros asigna los campos")
    void parameterizedConstructor() {
        Vehicle v = new Vehicle("Ana", "ABC-123", 30);
        assertEquals("Ana", v.getCliente());
        assertEquals("ABC-123", v.getPlaca());
        assertEquals(30, v.getMinutos());
        assertNull(v.getId());
        assertNull(v.getHoraIngreso());
    }

    @Test
    @DisplayName("Happy: constructor vacío y setters/getters")
    void settersAndGetters() {
        Vehicle v = new Vehicle();
        LocalDateTime now = LocalDateTime.now();
        v.setCliente("Luis");
        v.setPlaca("XYZ-999");
        v.setMinutos(10);
        v.setHoraIngreso(now);
        assertEquals("Luis", v.getCliente());
        assertEquals("XYZ-999", v.getPlaca());
        assertEquals(10, v.getMinutos());
        assertEquals(now, v.getHoraIngreso());
    }

    @Test
    @DisplayName("Happy: @PrePersist asigna la hora de ingreso cuando es nula")
    void prePersistSetsHoraIngresoWhenNull() {
        Vehicle v = new Vehicle("A", "P", 1);
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        v.registrarHoraIngreso();
        assertNotNull(v.getHoraIngreso());
        assertTrue(v.getHoraIngreso().isAfter(before));
    }

    @Test
    @DisplayName("Unhappy/borde: @PrePersist no sobrescribe una hora ya existente")
    void prePersistKeepsExistingHoraIngreso() {
        Vehicle v = new Vehicle("A", "P", 1);
        LocalDateTime fixed = LocalDateTime.of(2026, 1, 1, 8, 0);
        v.setHoraIngreso(fixed);
        v.registrarHoraIngreso();
        assertEquals(fixed, v.getHoraIngreso());
    }
}
