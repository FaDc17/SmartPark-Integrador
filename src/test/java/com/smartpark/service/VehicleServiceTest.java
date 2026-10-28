package com.smartpark.service;

import com.smartpark.dao.VehicleDao;
import com.smartpark.model.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock VehicleDao vehicleDao;
    @InjectMocks VehicleService service;

    private Vehicle vehicle(Long id, String cliente, String placa) {
        Vehicle v = new Vehicle(cliente, placa, 10);
        ReflectionTestUtils.setField(v, "id", id);
        return v;
    }

    // ---------- ensureDemoVehicles ----------
    @Test
    @DisplayName("Happy: crea los 5 vehículos demo cuando no existen")
    void demoVehiclesCreated() {
        when(vehicleDao.existsByPlaca(anyString())).thenReturn(false);
        service.ensureDemoVehicles();
        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleDao, times(5)).save(captor.capture());
        assertEquals("Carlos Mendoza", captor.getAllValues().get(0).getCliente());
        assertEquals("ABC-123", captor.getAllValues().get(0).getPlaca());
        assertEquals(8, captor.getAllValues().get(0).getHoraIngreso().getHour());
        assertEquals(35, captor.getAllValues().get(0).getHoraIngreso().getMinute());
    }

    @Test
    @DisplayName("Unhappy/borde: no duplica vehículos demo existentes")
    void demoVehiclesAlreadyExist() {
        when(vehicleDao.existsByPlaca(anyString())).thenReturn(true);
        service.ensureDemoVehicles();
        verify(vehicleDao, never()).save(any());
    }

    // ---------- findAll / findBySearch ----------
    @Test
    void findAll() {
        List<Vehicle> list = List.of(vehicle(1L, "Ana", "AAA-111"));
        when(vehicleDao.findActive()).thenReturn(list);
        assertSame(list, service.findAll());
    }

    @Test
    @DisplayName("findBySearch: null y blanco devuelven todos")
    void searchNullOrBlank() {
        List<Vehicle> list = List.of(vehicle(1L, "Ana", "AAA-111"));
        when(vehicleDao.findActive()).thenReturn(list);
        assertEquals(list, service.findBySearch(null));
        assertEquals(list, service.findBySearch("   "));
    }

    @Test
    @DisplayName("findBySearch: filtra por cliente, placa e id")
    void searchMatches() {
        Vehicle a = vehicle(1L, "Ana Lopez", "AAA-111");
        Vehicle b = vehicle(22L, "Luis", "ZZZ-999");
        Vehicle c = vehicle(33L, "Pedro", "QQQ-555");
        when(vehicleDao.findActive()).thenReturn(List.of(a, b, c));
        assertEquals(List.of(a), service.findBySearch("  ANA "));
        assertEquals(List.of(b), service.findBySearch("zzz"));
        assertEquals(List.of(b), service.findBySearch("22"));
    }

    @Test
    @DisplayName("Unhappy: findBySearch sin coincidencias devuelve lista vacía")
    void searchNoMatches() {
        when(vehicleDao.findActive()).thenReturn(List.of(vehicle(1L, "Ana", "AAA-111")));
        assertTrue(service.findBySearch("nada").isEmpty());
    }

    // ---------- addVehicle ----------
    @Test
    @DisplayName("Happy: addVehicle normaliza, usa 1 minuto y guarda")
    void addVehicle() {
        when(vehicleDao.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));
        Vehicle v = service.addVehicle("  Ana  ", " abc-123 ");
        assertEquals("Ana", v.getCliente());
        assertEquals("ABC-123", v.getPlaca());
        assertEquals(1, v.getMinutos());
    }

    @Test
    @DisplayName("Unhappy: addVehicle con cliente/placa nulos o vacíos")
    void addVehicleInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.addVehicle(null, "ABC"));
        assertThrows(IllegalArgumentException.class, () -> service.addVehicle("Ana", null));
        assertThrows(IllegalArgumentException.class, () -> service.addVehicle("  ", "ABC"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addVehicle("Ana", "   "));
        assertEquals("Ingresa el nombre del cliente y la placa del vehículo.", ex.getMessage());
        verify(vehicleDao, never()).save(any());
    }

    // ---------- updateVehicle ----------
    @Test
    @DisplayName("Happy: updateVehicle actualiza cliente y placa")
    void updateVehicle() {
        Vehicle v = vehicle(1L, "Ana", "AAA-111");
        when(vehicleDao.findById(1L)).thenReturn(Optional.of(v));
        when(vehicleDao.save(v)).thenReturn(v);
        Vehicle r = service.updateVehicle("1", "  Maria ", " bbb-222 ");
        assertEquals("Maria", r.getCliente());
        assertEquals("BBB-222", r.getPlaca());
    }

    @Test
    @DisplayName("Happy: updateVehicle con null conserva los valores")
    void updateVehicleNullsKeepValues() {
        Vehicle v = vehicle(1L, "Ana", "AAA-111");
        when(vehicleDao.findById(1L)).thenReturn(Optional.of(v));
        when(vehicleDao.save(v)).thenReturn(v);
        Vehicle r = service.updateVehicle("1", null, null);
        assertEquals("Ana", r.getCliente());
        assertEquals("AAA-111", r.getPlaca());
    }

    @Test
    @DisplayName("Unhappy: updateVehicle con vehículo inexistente o id inválido")
    void updateVehicleNotFound() {
        when(vehicleDao.findById(99L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateVehicle("99", "A", "B"));
        assertEquals("Vehículo no encontrado.", ex.getMessage());
        assertThrows(IllegalArgumentException.class, () -> service.updateVehicle("abc", "A", "B"));
        verify(vehicleDao, never()).save(any());
    }

    // ---------- findById ----------
    @Test
    void findByIdFound() {
        Vehicle v = vehicle(5L, "A", "B");
        when(vehicleDao.findById(5L)).thenReturn(Optional.of(v));
        assertSame(v, service.findById("5"));
    }

    @Test
    @DisplayName("Unhappy: findById no existe / id no numérico / null")
    void findByIdInvalid() {
        when(vehicleDao.findById(5L)).thenReturn(Optional.empty());
        assertNull(service.findById("5"));
        assertNull(service.findById("xyz"));
        assertNull(service.findById(null));
    }

    // ---------- removeVehicle ----------
    @Test
    void removeVehicleExisting() {
        Vehicle v = vehicle(1L, "A", "B");
        when(vehicleDao.findById(1L)).thenReturn(Optional.of(v));
        service.removeVehicle("1");
        verify(vehicleDao).delete(v);
    }

    @Test
    @DisplayName("Unhappy: removeVehicle inexistente no elimina nada")
    void removeVehicleMissing() {
        when(vehicleDao.findById(1L)).thenReturn(Optional.empty());
        service.removeVehicle("1");
        service.removeVehicle("abc");
        verify(vehicleDao, never()).delete(any());
    }

    // ---------- espacios ----------
    @Test
    void occupiedAndFreeSpaces() {
        when(vehicleDao.countActive()).thenReturn(30L);
        assertEquals(30, service.getOccupiedSpaces());
        assertEquals(70, service.getFreeSpaces());
    }

    @Test
    @DisplayName("Borde: libres nunca es negativo cuando se supera la capacidad")
    void freeSpacesNeverNegative() {
        when(vehicleDao.countActive()).thenReturn(150L);
        assertEquals(0, service.getFreeSpaces());
    }

    @Test
    @DisplayName("Unhappy: getOccupiedSpaces con conteo fuera de rango int lanza ArithmeticException")
    void occupiedOverflow() {
        when(vehicleDao.countActive()).thenReturn(Long.MAX_VALUE);
        assertThrows(ArithmeticException.class, () -> service.getOccupiedSpaces());
    }

    @Test
    void weeklyUsePercentage() {
        assertEquals(78.57, service.getWeeklyUsePercentage());
    }

    @Test
    @DisplayName("randomHour siempre devuelve HH:mm dentro de 07:00-17:59")
    void randomHour() {
        for (int i = 0; i < 200; i++) {
            String h = service.randomHour();
            assertTrue(h.matches("\\d{2}:\\d{2}"), h);
            int hour = Integer.parseInt(h.substring(0, 2));
            int min = Integer.parseInt(h.substring(3));
            assertTrue(hour >= 7 && hour <= 17, h);
            assertTrue(min >= 0 && min <= 59, h);
        }
    }
}
