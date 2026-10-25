package com.smartpark.service;

import com.smartpark.dao.DetallePagoDao;
import com.smartpark.dao.PagoDao;
import com.smartpark.dao.VehicleDao;
import com.smartpark.model.DetallePago;
import com.smartpark.model.Pago;
import com.smartpark.model.Vehicle;
import com.smartpark.payment.EfectivoStrategy;
import com.smartpark.payment.TarjetaStrategy;
import com.smartpark.payment.YapeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock PagoDao pagoDao;
    @Mock DetallePagoDao detallePagoDao;
    @Mock VehicleDao vehicleDao;

    private PagoService service;
    private Vehicle vehicle;
    private String prefix;

    @BeforeEach
    void setUp() {
        service = new PagoService(pagoDao, detallePagoDao, vehicleDao, new ParkingService(),
                List.of(new EfectivoStrategy(), new TarjetaStrategy(), new YapeStrategy()));
        vehicle = new Vehicle("Ana", "ABC-123", 60);
        vehicle.setHoraIngreso(LocalDateTime.of(2026, 1, 1, 8, 0));
        ReflectionTestUtils.setField(vehicle, "id", 1L);
        prefix = "SP-" + LocalDate.now().getYear() + "-";
    }

    /** Configura el camino feliz hasta antes de generar el código. */
    private void stubReadyToPay() {
        when(vehicleDao.findByIdForUpdate(1L)).thenReturn(Optional.of(vehicle));
        when(pagoDao.findLatestByVehicleId(1L)).thenReturn(Optional.empty());
    }

    private void stubLockAndCodes(List<String> codes) {
        when(pagoDao.acquirePaymentCodeLock()).thenReturn(true);
        when(pagoDao.findPaymentCodesByPrefix(prefix)).thenReturn(codes);
    }

    // ---------- registrarPago: happy ----------
    @Test
    @DisplayName("Happy: primer pago de la anualidad usa el número inicial 576450")
    void registrarPagoFirstCode() {
        stubReadyToPay();
        stubLockAndCodes(List.of());
        when(pagoDao.saveAndFlush(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        Pago result = service.registrarPago(1L, "Efectivo", new BigDecimal("10.00"));

        assertEquals(prefix + "576450", result.getCodigoPago());
        assertEquals(new BigDecimal("6.00"), result.getMonto());
        assertEquals("Efectivo", result.getMetodoPago());
        assertEquals("PAGADO", result.getEstado());
        assertSame(vehicle, result.getVehicle());
        assertNotNull(result.getFechaPago());

        ArgumentCaptor<DetallePago> c = ArgumentCaptor.forClass(DetallePago.class);
        verify(detallePagoDao).save(c.capture());
        DetallePago d = c.getValue();
        assertSame(result, d.getPago());
        assertEquals("Ana", d.getCliente());
        assertEquals("ABC-123", d.getPlaca());
        assertEquals(vehicle.getHoraIngreso(), d.getHoraIngreso());
        assertEquals(60, d.getMinutosEstacionado());
        assertEquals(new BigDecimal("6.00"), d.getTotal());
        assertNotNull(d.getHoraSalida());
    }

    @Test
    @DisplayName("Happy: el código es correlativo al mayor existente")
    void registrarPagoIncrementsHighest() {
        stubReadyToPay();
        stubLockAndCodes(List.of(prefix + "576450", prefix + "576460", prefix + "576455"));
        when(pagoDao.saveAndFlush(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        Pago result = service.registrarPago(1L, "Tarjeta", null);

        assertEquals(prefix + "576461", result.getCodigoPago());
        assertEquals("Tarjeta", result.getMetodoPago());
    }

    @Test
    @DisplayName("Happy: método de pago insensible a mayúsculas y espacios (Yape sin monto)")
    void registrarPagoMethodNormalized() {
        stubReadyToPay();
        stubLockAndCodes(List.of());
        when(pagoDao.saveAndFlush(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        Pago result = service.registrarPago(1L, "  YAPE ", null);

        assertEquals("Yape", result.getMetodoPago());
    }

    @Test
    @DisplayName("Happy (idempotencia): si ya existe pago lo devuelve sin registrar otro")
    void registrarPagoIdempotent() {
        Pago existing = new Pago();
        when(vehicleDao.findByIdForUpdate(1L)).thenReturn(Optional.of(vehicle));
        when(pagoDao.findLatestByVehicleId(1L)).thenReturn(Optional.of(existing));

        assertSame(existing, service.registrarPago(1L, "Efectivo", BigDecimal.TEN));

        verify(pagoDao, never()).saveAndFlush(any());
        verify(detallePagoDao, never()).save(any());
        verify(pagoDao, never()).acquirePaymentCodeLock();
    }

    // ---------- registrarPago: unhappy ----------
    @Test
    @DisplayName("Unhappy: vehículo no encontrado")
    void registrarPagoVehicleNotFound() {
        when(vehicleDao.findByIdForUpdate(1L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrarPago(1L, "Efectivo", BigDecimal.TEN));
        assertEquals("Vehículo no encontrado.", ex.getMessage());
    }

    @Test
    @DisplayName("Unhappy: método de pago nulo")
    void registrarPagoNullMethod() {
        stubReadyToPay();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrarPago(1L, null, BigDecimal.TEN));
        assertEquals("Selecciona un método de pago válido.", ex.getMessage());
    }

    @Test
    @DisplayName("Unhappy: método de pago desconocido")
    void registrarPagoUnknownMethod() {
        stubReadyToPay();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrarPago(1L, "Bitcoin", BigDecimal.TEN));
        assertEquals("Selecciona un método de pago válido.", ex.getMessage());
        verify(pagoDao, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Unhappy: efectivo insuficiente")
    void registrarPagoInsufficientCash() {
        stubReadyToPay();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.registrarPago(1L, "Efectivo", new BigDecimal("5.99")));
        assertEquals("El monto recibido no cubre el total a pagar.", ex.getMessage());
        verify(pagoDao, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Unhappy: efectivo sin monto recibido")
    void registrarPagoCashWithoutAmount() {
        stubReadyToPay();
        assertThrows(IllegalArgumentException.class, () -> service.registrarPago(1L, "Efectivo", null));
    }

    @Test
    @DisplayName("Unhappy: no se puede tomar el bloqueo del código de pago")
    void registrarPagoLockFails() {
        stubReadyToPay();
        when(pagoDao.acquirePaymentCodeLock()).thenReturn(false);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.registrarPago(1L, "Tarjeta", null));
        assertEquals("No se pudo bloquear la generación del código de pago.", ex.getMessage());
        verify(pagoDao, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Unhappy: existe un código con sufijo no numérico")
    void registrarPagoInvalidExistingCode() {
        stubReadyToPay();
        stubLockAndCodes(List.of(prefix + "ABC"));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.registrarPago(1L, "Tarjeta", null));
        assertTrue(ex.getMessage().contains("formato inválido"));
    }

    @Test
    @DisplayName("Unhappy: existe un código con sufijo vacío")
    void registrarPagoEmptySuffixCode() {
        stubReadyToPay();
        stubLockAndCodes(List.of(prefix));
        assertThrows(IllegalStateException.class, () -> service.registrarPago(1L, "Tarjeta", null));
    }

    @Test
    @DisplayName("Unhappy: sufijo numérico que excede el rango de long")
    void registrarPagoNumberTooLarge() {
        stubReadyToPay();
        stubLockAndCodes(List.of(prefix + "99999999999999999999"));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.registrarPago(1L, "Tarjeta", null));
        assertEquals("El número de un código de pago excede el límite permitido.", ex.getMessage());
        assertInstanceOf(NumberFormatException.class, ex.getCause());
    }

    @Test
    @DisplayName("Unhappy: el siguiente número desborda long (Long.MAX_VALUE)")
    void registrarPagoOverflowOnIncrement() {
        stubReadyToPay();
        stubLockAndCodes(List.of(prefix + Long.MAX_VALUE));
        assertThrows(ArithmeticException.class, () -> service.registrarPago(1L, "Tarjeta", null));
    }

    // ---------- consultas ----------
    @Test
    void findByIdAndVehicleId() {
        Pago p = new Pago();
        when(pagoDao.findByIdAndVehicleId(2L, 1L)).thenReturn(Optional.of(p));
        when(pagoDao.findByIdAndVehicleId(3L, 1L)).thenReturn(Optional.empty());
        assertEquals(Optional.of(p), service.findByIdAndVehicleId(2L, 1L));
        assertTrue(service.findByIdAndVehicleId(3L, 1L).isEmpty());
    }

    @Test
    void findDetailByPaymentId() {
        DetallePago d = new DetallePago();
        when(detallePagoDao.findByPaymentId(2L)).thenReturn(Optional.of(d));
        when(detallePagoDao.findByPaymentId(3L)).thenReturn(Optional.empty());
        assertEquals(Optional.of(d), service.findDetailByPaymentId(2L));
        assertTrue(service.findDetailByPaymentId(3L).isEmpty());
    }

    @Test
    void hasPaymentForVehicle() {
        when(pagoDao.existsByVehicleId(1L)).thenReturn(true);
        when(pagoDao.existsByVehicleId(2L)).thenReturn(false);
        assertTrue(service.hasPaymentForVehicle(1L));
        assertFalse(service.hasPaymentForVehicle(2L));
    }

    // ---------- historial ----------
    private DetallePago detalle(Long id, String cliente, String placa) {
        DetallePago d = new DetallePago();
        ReflectionTestUtils.setField(d, "id", id);
        d.setCliente(cliente);
        d.setPlaca(placa);
        return d;
    }

    @Test
    @DisplayName("Happy: historial sin búsqueda (null o en blanco) devuelve todo")
    void historialWithoutQuery() {
        List<DetallePago> all = List.of(detalle(2L, "Ana", "AAA-111"), detalle(1L, "Luis", "BBB-222"));
        when(detallePagoDao.findAllLatestFirst()).thenReturn(all);
        assertSame(all, service.findHistorial(null));
        assertSame(all, service.findHistorial("   "));
    }

    @Test
    @DisplayName("Happy: historial filtra por cliente, placa e ID (sin distinguir mayúsculas)")
    void historialFilters() {
        DetallePago a = detalle(2L, "Ana Lopez", "AAA-111");
        DetallePago b = detalle(35L, "Luis", "ZZZ-999");
        when(detallePagoDao.findAllLatestFirst()).thenReturn(List.of(a, b));
        assertEquals(List.of(a), service.findHistorial("  ANA "));
        assertEquals(List.of(b), service.findHistorial("zzz"));
        assertEquals(List.of(b), service.findHistorial("35"));
    }

    @Test
    @DisplayName("Unhappy: historial sin coincidencias devuelve lista vacía")
    void historialNoMatches() {
        when(detallePagoDao.findAllLatestFirst()).thenReturn(List.of(detalle(1L, "Ana", "AAA-111")));
        assertTrue(service.findHistorial("nada").isEmpty());
    }

    @Test
    @DisplayName("Borde: historial tolera cliente/placa/id nulos sin lanzar excepción")
    void historialNullFields() {
        DetallePago empty = new DetallePago();
        when(detallePagoDao.findAllLatestFirst()).thenReturn(List.of(empty));
        assertTrue(service.findHistorial("x").isEmpty());
    }

    @Test
    @DisplayName("Borde: historial vacío")
    void historialEmpty() {
        when(detallePagoDao.findAllLatestFirst()).thenReturn(List.of());
        assertTrue(service.findHistorial("a").isEmpty());
        assertTrue(service.findHistorial(null).isEmpty());
    }
}
