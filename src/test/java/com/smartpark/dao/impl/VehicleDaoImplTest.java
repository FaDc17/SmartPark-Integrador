package com.smartpark.dao.impl;

import com.smartpark.model.Vehicle;
import com.smartpark.repository.VehicleRepository;
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
class VehicleDaoImplTest {

    @Mock VehicleRepository repository;
    @InjectMocks VehicleDaoImpl dao;

    @Test
    void save() {
        Vehicle v = new Vehicle();
        when(repository.save(v)).thenReturn(v);
        assertSame(v, dao.save(v));
    }

    @Test
    @DisplayName("Happy: findById encontrado")
    void findByIdFound() {
        Vehicle v = new Vehicle();
        when(repository.findById(1L)).thenReturn(Optional.of(v));
        assertEquals(Optional.of(v), dao.findById(1L));
    }

    @Test
    @DisplayName("Unhappy: findById no encontrado")
    void findByIdEmpty() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertTrue(dao.findById(1L).isEmpty());
    }

    @Test
    @DisplayName("findByIdForUpdate encontrado y no encontrado")
    void findForUpdate() {
        Vehicle v = new Vehicle();
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(v));
        when(repository.findByIdForUpdate(2L)).thenReturn(Optional.empty());
        assertEquals(Optional.of(v), dao.findByIdForUpdate(1L));
        assertTrue(dao.findByIdForUpdate(2L).isEmpty());
    }

    @Test
    void findActive() {
        List<Vehicle> list = List.of(new Vehicle());
        when(repository.findActiveVehicles()).thenReturn(list);
        assertSame(list, dao.findActive());
    }

    @Test
    void countActive() {
        when(repository.countActiveVehicles()).thenReturn(4L);
        assertEquals(4L, dao.countActive());
    }

    @Test
    void existsByPlaca() {
        when(repository.existsByPlaca("A")).thenReturn(true);
        when(repository.existsByPlaca("B")).thenReturn(false);
        assertTrue(dao.existsByPlaca("A"));
        assertFalse(dao.existsByPlaca("B"));
    }

    @Test
    void delete() {
        Vehicle v = new Vehicle();
        dao.delete(v);
        verify(repository).delete(v);
    }
}
