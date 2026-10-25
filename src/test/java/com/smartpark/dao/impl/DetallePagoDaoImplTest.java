package com.smartpark.dao.impl;

import java.util.List;
import com.smartpark.model.DetallePago;
import com.smartpark.repository.DetallePagoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetallePagoDaoImplTest {

    @Mock DetallePagoRepository repository;
    @InjectMocks DetallePagoDaoImpl dao;

    @Test
    @DisplayName("Happy: save delega en el repositorio")
    void save() {
        DetallePago d = new DetallePago();
        when(repository.save(d)).thenReturn(d);
        assertSame(d, dao.save(d));
        verify(repository).save(d);
    }

    @Test
    @DisplayName("Happy: findByPaymentId encuentra el detalle")
    void findFound() {
        DetallePago d = new DetallePago();
        when(repository.findById(5L)).thenReturn(Optional.of(d));
        assertEquals(Optional.of(d), dao.findByPaymentId(5L));
    }

    @Test
    @DisplayName("Unhappy: findByPaymentId sin resultado")
    void findNotFound() {
        when(repository.findById(9L)).thenReturn(Optional.empty());
        assertTrue(dao.findByPaymentId(9L).isEmpty());
    }

    @Test
    @DisplayName("Happy: findAllLatestFirst delega en el repositorio")
    void findAllLatestFirst() {
        List<DetallePago> list = List.of(new DetallePago(), new DetallePago());
        when(repository.findAllByOrderByIdDesc()).thenReturn(list);
        assertSame(list, dao.findAllLatestFirst());
    }

    @Test
    @DisplayName("Borde: findAllLatestFirst sin pagos devuelve lista vacía")
    void findAllLatestFirstEmpty() {
        when(repository.findAllByOrderByIdDesc()).thenReturn(List.of());
        assertTrue(dao.findAllLatestFirst().isEmpty());
    }
}
