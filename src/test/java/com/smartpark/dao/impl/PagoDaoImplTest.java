package com.smartpark.dao.impl;

import com.smartpark.model.Pago;
import com.smartpark.repository.PagoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoDaoImplTest {

    @Mock PagoRepository repository;
    @InjectMocks PagoDaoImpl dao;

    @Test
    @DisplayName("Happy: saveAndFlush delega en el repositorio")
    void saveAndFlush() {
        Pago p = new Pago();
        when(repository.saveAndFlush(p)).thenReturn(p);
        assertSame(p, dao.saveAndFlush(p));
    }

    @Test
    @DisplayName("Happy: findByIdAndVehicleId encontrado")
    void findByIdAndVehicleIdFound() {
        Pago p = new Pago();
        when(repository.findByIdAndVehicle_Id(1L, 2L)).thenReturn(Optional.of(p));
        assertEquals(Optional.of(p), dao.findByIdAndVehicleId(1L, 2L));
    }

    @Test
    @DisplayName("Unhappy: findByIdAndVehicleId no encontrado")
    void findByIdAndVehicleIdEmpty() {
        when(repository.findByIdAndVehicle_Id(1L, 2L)).thenReturn(Optional.empty());
        assertTrue(dao.findByIdAndVehicleId(1L, 2L).isEmpty());
    }

    @Test
    @DisplayName("Happy: findLatestByVehicleId")
    void findLatest() {
        Pago p = new Pago();
        when(repository.findFirstByVehicle_IdOrderByIdDesc(3L)).thenReturn(Optional.of(p));
        assertEquals(Optional.of(p), dao.findLatestByVehicleId(3L));
    }

    @Test
    @DisplayName("Unhappy: findLatestByVehicleId sin pagos")
    void findLatestEmpty() {
        when(repository.findFirstByVehicle_IdOrderByIdDesc(3L)).thenReturn(Optional.empty());
        assertTrue(dao.findLatestByVehicleId(3L).isEmpty());
    }

    @Test
    @DisplayName("existsByVehicleId verdadero y falso")
    void exists() {
        when(repository.existsByVehicle_Id(1L)).thenReturn(true);
        when(repository.existsByVehicle_Id(2L)).thenReturn(false);
        assertTrue(dao.existsByVehicleId(1L));
        assertFalse(dao.existsByVehicleId(2L));
    }

    @Test
    @DisplayName("findPaymentCodesByPrefix devuelve códigos")
    void codes() {
        when(repository.findPaymentCodesByPrefix("SP-2026-")).thenReturn(List.of("SP-2026-000001"));
        assertEquals(List.of("SP-2026-000001"), dao.findPaymentCodesByPrefix("SP-2026-"));
    }

    @Test
    @DisplayName("acquirePaymentCodeLock devuelve lo que responde el repositorio")
    void lock() {
        when(repository.acquirePaymentCodeLock()).thenReturn(true, false);
        assertTrue(dao.acquirePaymentCodeLock());
        assertFalse(dao.acquirePaymentCodeLock());
    }
}
