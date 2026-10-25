package com.smartpark.controller;

import com.smartpark.model.DetallePago;
import com.smartpark.model.Pago;
import com.smartpark.model.Vehicle;
import com.smartpark.service.PagoService;
import com.smartpark.service.ParkingService;
import com.smartpark.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmartParkControllerTest {

    @Mock VehicleService vehicleService;
    @Mock ParkingService parkingService;
    @Mock PagoService pagoService;

    private SmartParkController controller;
    private MockHttpSession loggedIn;
    private MockHttpSession anonymous;
    private Model model;
    private RedirectAttributesModelMap redirect;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        controller = new SmartParkController(vehicleService, parkingService, pagoService);
        loggedIn = new MockHttpSession();
        loggedIn.setAttribute("loggedIn", true);
        loggedIn.setAttribute("username", "user@x.com");
        anonymous = new MockHttpSession();
        model = new ExtendedModelMap();
        redirect = new RedirectAttributesModelMap();
        vehicle = new Vehicle("Ana", "ABC-123", 60);
        ReflectionTestUtils.setField(vehicle, "id", 1L);
    }

    // ---------- dashboard ----------
    @Test
    @DisplayName("Happy: dashboard carga los datos del modelo")
    void dashboard() {
        List<Vehicle> list = List.of(vehicle);
        when(vehicleService.findAll()).thenReturn(list);
        when(vehicleService.getOccupiedSpaces()).thenReturn(1);
        when(vehicleService.getFreeSpaces()).thenReturn(99);
        when(parkingService.calculateCost(60)).thenReturn(new BigDecimal("6.00"));
        when(parkingService.calculateCost(1)).thenReturn(new BigDecimal("0.10"));

        assertEquals("dashboard", controller.dashboard(loggedIn, model));
        assertSame(list, model.getAttribute("vehicles"));
        assertEquals(1, model.getAttribute("occupied"));
        assertEquals(99, model.getAttribute("free"));
        assertEquals(new BigDecimal("6.00"), model.getAttribute("ratePerHour"));
        assertEquals(new BigDecimal("0.10"), model.getAttribute("ratePerMinute"));
        assertEquals("user@x.com", model.getAttribute("username"));
    }

    @Test
    @DisplayName("Unhappy: dashboard sin sesión redirige a login")
    void dashboardAnonymous() {
        assertEquals("redirect:/login", controller.dashboard(anonymous, model));
        verifyNoInteractions(vehicleService);
    }

    // ---------- registrar (GET) ----------
    @Test
    void registrarPage() {
        assertEquals("registrar", controller.registrar(loggedIn, model));
        assertInstanceOf(Vehicle.class, model.getAttribute("vehicle"));
    }

    @Test
    void registrarPageAnonymous() {
        assertEquals("redirect:/login", controller.registrar(anonymous, model));
    }

    // ---------- registrar (POST) ----------
    @Test
    @DisplayName("Happy: registrar vehículo")
    void registrarSubmitOk() {
        assertEquals("redirect:/modulo", controller.registrarSubmit("Ana", "ABC", loggedIn, redirect));
        verify(vehicleService).addVehicle("Ana", "ABC");
        assertEquals("Vehículo registrado correctamente.", redirect.getFlashAttributes().get("success"));
    }

    @Test
    @DisplayName("Unhappy: registrar con datos inválidos")
    void registrarSubmitInvalid() {
        when(vehicleService.addVehicle("", "")).thenThrow(new IllegalArgumentException("msg"));
        assertEquals("redirect:/registrar", controller.registrarSubmit("", "", loggedIn, redirect));
        assertEquals("msg", redirect.getFlashAttributes().get("error"));
    }

    @Test
    @DisplayName("Unhappy: registrar sin sesión")
    void registrarSubmitAnonymous() {
        assertEquals("redirect:/login", controller.registrarSubmit("A", "B", anonymous, redirect));
        verifyNoInteractions(vehicleService);
    }

    // ---------- modulo (GET) ----------
    @Test
    @DisplayName("Happy: módulo con búsqueda")
    void moduloWithSearch() {
        when(vehicleService.findBySearch("ana")).thenReturn(List.of(vehicle));
        assertEquals("modulo", controller.modulo(loggedIn, model, "ana"));
        assertEquals("ana", model.getAttribute("search"));
        assertEquals(List.of(vehicle), model.getAttribute("vehicles"));
    }

    @Test
    @DisplayName("Borde: módulo sin búsqueda usa cadena vacía")
    void moduloWithoutSearch() {
        when(vehicleService.findBySearch(null)).thenReturn(List.of());
        assertEquals("modulo", controller.modulo(loggedIn, model, null));
        assertEquals("", model.getAttribute("search"));
    }

    @Test
    void moduloAnonymous() {
        assertEquals("redirect:/login", controller.modulo(anonymous, model, null));
    }

    // ---------- pago (GET) ----------
    @Test
    @DisplayName("Happy: vista de pago sin pago previo")
    void pagoWithoutPayment() {
        when(vehicleService.findById("1")).thenReturn(vehicle);
        when(parkingService.calculateCost(60)).thenReturn(new BigDecimal("6.00"));
        when(parkingService.formatCurrency(new BigDecimal("6.00"))).thenReturn("S/ 6.00");
        when(parkingService.formatMinutes(60)).thenReturn("1 h");

        assertEquals("pago", controller.pago("1", null, loggedIn, model));
        assertSame(vehicle, model.getAttribute("vehicle"));
        assertEquals(new BigDecimal("6.00"), model.getAttribute("total"));
        assertEquals("S/ 6.00", model.getAttribute("totalText"));
        assertNull(model.getAttribute("payment"));
        assertNull(model.getAttribute("detail"));
        assertEquals("S/ 6.00", model.getAttribute("receiptTotalText"));
        assertEquals("1 h", model.getAttribute("receiptTimeText"));
    }

    @Test
    @DisplayName("Happy: vista de pago con boleta (paymentId)")
    void pagoWithPayment() {
        Pago pago = new Pago();
        pago.setMonto(new BigDecimal("6.00"));
        ReflectionTestUtils.setField(pago, "id", 10L);
        DetallePago detail = new DetallePago();
        detail.setMinutosEstacionado(90);
        when(vehicleService.findById("1")).thenReturn(vehicle);
        when(parkingService.calculateCost(60)).thenReturn(new BigDecimal("6.00"));
        when(parkingService.formatCurrency(new BigDecimal("6.00"))).thenReturn("S/ 6.00");
        when(pagoService.findByIdAndVehicleId(10L, 1L)).thenReturn(Optional.of(pago));
        when(pagoService.findDetailByPaymentId(10L)).thenReturn(Optional.of(detail));
        when(parkingService.formatMinutes(90)).thenReturn("1 h 30 min");

        assertEquals("pago", controller.pago("1", 10L, loggedIn, model));
        assertSame(pago, model.getAttribute("payment"));
        assertSame(detail, model.getAttribute("detail"));
        assertEquals("S/ 6.00", model.getAttribute("receiptTotalText"));
        assertEquals("1 h 30 min", model.getAttribute("receiptTimeText"));
    }

    @Test
    @DisplayName("Unhappy: pago sin sesión")
    void pagoAnonymous() {
        assertEquals("redirect:/login", controller.pago("1", null, anonymous, model));
    }

    @Test
    @DisplayName("Unhappy: pago con vehículo inexistente")
    void pagoVehicleMissing() {
        when(vehicleService.findById("9")).thenReturn(null);
        assertEquals("redirect:/modulo", controller.pago("9", null, loggedIn, model));
    }

    @Test
    @DisplayName("Unhappy: paymentId que no pertenece al vehículo")
    void pagoPaymentNotOfVehicle() {
        when(vehicleService.findById("1")).thenReturn(vehicle);
        when(parkingService.calculateCost(60)).thenReturn(new BigDecimal("6.00"));
        when(pagoService.findByIdAndVehicleId(77L, 1L)).thenReturn(Optional.empty());
        assertEquals("redirect:/modulo", controller.pago("1", 77L, loggedIn, model));
    }

    @Test
    @DisplayName("Unhappy: pago sin detalle de boleta lanza IllegalStateException")
    void pagoWithoutDetail() {
        Pago pago = new Pago();
        ReflectionTestUtils.setField(pago, "id", 10L);
        when(vehicleService.findById("1")).thenReturn(vehicle);
        when(parkingService.calculateCost(60)).thenReturn(new BigDecimal("6.00"));
        when(pagoService.findByIdAndVehicleId(10L, 1L)).thenReturn(Optional.of(pago));
        when(pagoService.findDetailByPaymentId(10L)).thenReturn(Optional.empty());
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> controller.pago("1", 10L, loggedIn, model));
        assertEquals("El pago no tiene un detalle de boleta asociado.", ex.getMessage());
    }

    // ---------- confirmarPago ----------
    @Test
    @DisplayName("Happy: confirmar pago redirige con paymentId")
    void confirmarPagoOk() {
        Pago pago = new Pago();
        ReflectionTestUtils.setField(pago, "id", 10L);
        when(vehicleService.findById("1")).thenReturn(vehicle);
        when(pagoService.registrarPago(1L, "Efectivo", BigDecimal.TEN)).thenReturn(pago);

        assertEquals("redirect:/pago/1",
                controller.confirmarPago("1", "Efectivo", BigDecimal.TEN, loggedIn, redirect));
        // RedirectAttributes convierte los atributos de URL a String
        assertEquals("10", redirect.get("paymentId"));
    }

    @Test
    @DisplayName("Unhappy: confirmar pago sin sesión")
    void confirmarPagoAnonymous() {
        assertEquals("redirect:/login",
                controller.confirmarPago("1", "Efectivo", null, anonymous, redirect));
    }

    @Test
    @DisplayName("Unhappy: confirmar pago con vehículo inexistente")
    void confirmarPagoVehicleMissing() {
        when(vehicleService.findById("9")).thenReturn(null);
        assertEquals("redirect:/modulo",
                controller.confirmarPago("9", "Efectivo", null, loggedIn, redirect));
        verifyNoInteractions(pagoService);
    }

    @Test
    @DisplayName("Unhappy: error de validación al registrar el pago")
    void confirmarPagoValidationError() {
        when(vehicleService.findById("1")).thenReturn(vehicle);
        when(pagoService.registrarPago(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Selecciona un método de pago válido."));
        assertEquals("redirect:/pago/1",
                controller.confirmarPago("1", "X", null, loggedIn, redirect));
        assertEquals("Selecciona un método de pago válido.", redirect.getFlashAttributes().get("error"));
    }

    // ---------- guardarVehiculoDesdeModal ----------
    @Test
    @DisplayName("Happy: actualizar vehículo")
    void guardarVehiculoOk() {
        assertEquals("redirect:/modulo",
                controller.guardarVehiculoDesdeModal("1", "Ana", "ABC", loggedIn, redirect));
        verify(vehicleService).updateVehicle("1", "Ana", "ABC");
        assertEquals("Datos actualizados correctamente.", redirect.getFlashAttributes().get("success"));
    }

    @Test
    @DisplayName("Unhappy: actualizar vehículo inexistente")
    void guardarVehiculoError() {
        when(vehicleService.updateVehicle("9", "A", "B")).thenThrow(new IllegalArgumentException("Vehículo no encontrado."));
        assertEquals("redirect:/modulo",
                controller.guardarVehiculoDesdeModal("9", "A", "B", loggedIn, redirect));
        assertEquals("Vehículo no encontrado.", redirect.getFlashAttributes().get("error"));
    }

    @Test
    @DisplayName("Unhappy: actualizar vehículo sin sesión")
    void guardarVehiculoAnonymous() {
        assertEquals("redirect:/login",
                controller.guardarVehiculoDesdeModal("1", "A", "B", anonymous, redirect));
        verifyNoInteractions(vehicleService);
    }

    // ---------- finalizarPago ----------
    @Test
    @DisplayName("Happy: finalizar con pago registrado vuelve al módulo")
    void finalizarOk() {
        when(vehicleService.findById("1")).thenReturn(vehicle);
        when(pagoService.hasPaymentForVehicle(1L)).thenReturn(true);
        assertEquals("redirect:/modulo", controller.finalizarPago("1", loggedIn));
    }

    @Test
    @DisplayName("Unhappy: finalizar sin pago registrado vuelve a la pantalla de pago")
    void finalizarWithoutPayment() {
        when(vehicleService.findById("1")).thenReturn(vehicle);
        when(pagoService.hasPaymentForVehicle(1L)).thenReturn(false);
        assertEquals("redirect:/pago/1", controller.finalizarPago("1", loggedIn));
    }

    @Test
    @DisplayName("Unhappy: finalizar con vehículo inexistente")
    void finalizarVehicleMissing() {
        when(vehicleService.findById("7")).thenReturn(null);
        assertEquals("redirect:/pago/7", controller.finalizarPago("7", loggedIn));
        verifyNoInteractions(pagoService);
    }

    @Test
    @DisplayName("Unhappy: finalizar sin sesión")
    void finalizarAnonymous() {
        assertEquals("redirect:/login", controller.finalizarPago("1", anonymous));
    }

    // ---------- datos ----------
    @Test
    @DisplayName("Happy: administrador ve datos y estadísticas")
    void datosAdmin() {
        loggedIn.setAttribute("rol", "ADMIN");
        when(parkingService.buildWeeklyStayData()).thenReturn(List.of());
        when(parkingService.buildDailyVehicleData()).thenReturn(List.of());
        when(parkingService.buildMonthlyIncomeData()).thenReturn(List.of());
        when(parkingService.getAnnualIncomeTotal()).thenReturn(53500);

        assertEquals("datos", controller.datos(loggedIn, model));
        assertNotNull(model.getAttribute("espaciosData"));
        assertEquals(2, ((List<?>) model.getAttribute("espaciosData")).size());
        assertEquals(53500, model.getAttribute("annualIncomeTotal"));
        assertNotNull(model.getAttribute("tiempoSemanaData"));
        assertNotNull(model.getAttribute("vehiculosDiaData"));
        assertNotNull(model.getAttribute("ingresosData"));
    }

    @Test
    @DisplayName("Unhappy: cajero no puede ver datos")
    void datosCajero() {
        loggedIn.setAttribute("rol", "CAJERO");
        assertEquals("redirect:/dashboard", controller.datos(loggedIn, model));
    }

    @Test
    @DisplayName("Unhappy: datos sin sesión")
    void datosAnonymous() {
        assertEquals("redirect:/login", controller.datos(anonymous, model));
    }

    // ---------- historial ----------
    @Test
    @DisplayName("Happy: cajero ve el historial con búsqueda")
    void historialCajero() {
        loggedIn.setAttribute("rol", "CAJERO");
        List<DetallePago> list = List.of(new DetallePago());
        when(pagoService.findHistorial("ana")).thenReturn(list);

        assertEquals("historial", controller.historial(loggedIn, model, "ana"));
        assertSame(list, model.getAttribute("historial"));
        assertEquals("ana", model.getAttribute("search"));
    }

    @Test
    @DisplayName("Happy: administrador ve el historial")
    void historialAdmin() {
        loggedIn.setAttribute("rol", "ADMIN");
        when(pagoService.findHistorial(null)).thenReturn(List.of());

        assertEquals("historial", controller.historial(loggedIn, model, null));
        assertEquals(List.of(), model.getAttribute("historial"));
    }

    @Test
    @DisplayName("Borde: sin búsqueda el modelo lleva cadena vacía")
    void historialWithoutSearch() {
        when(pagoService.findHistorial(null)).thenReturn(List.of());
        controller.historial(loggedIn, model, null);
        assertEquals("", model.getAttribute("search"));
    }

    @Test
    @DisplayName("Unhappy: historial sin sesión redirige a login")
    void historialAnonymous() {
        assertEquals("redirect:/login", controller.historial(anonymous, model, null));
        verifyNoInteractions(pagoService);
    }
}
