package com.smartpark.controller;

import com.smartpark.model.Vehicle;
import com.smartpark.model.DetallePago;
import com.smartpark.model.Pago;
import com.smartpark.service.PagoService;
import com.smartpark.service.ParkingService;
import com.smartpark.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class SmartParkController {
    private final VehicleService vehicleService;
    private final ParkingService parkingService;
    private final PagoService pagoService;

    public SmartParkController(VehicleService vehicleService, ParkingService parkingService, PagoService pagoService) {
        this.vehicleService = vehicleService;
        this.parkingService = parkingService;
        this.pagoService = pagoService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedIn") != null;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        List<Vehicle> vehicles = vehicleService.findAll();
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("occupied", vehicleService.getOccupiedSpaces());
        model.addAttribute("free", vehicleService.getFreeSpaces());
        model.addAttribute("ratePerHour", parkingService.calculateCost(60));
        model.addAttribute("ratePerMinute", parkingService.calculateCost(1));
        model.addAttribute("username", session.getAttribute("username"));
        return "dashboard";
    }

    @GetMapping("/registrar")
    public String registrar(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        model.addAttribute("vehicle", new Vehicle());
        return "registrar";
    }

    @PostMapping("/registrar")
    public String registrarSubmit(@RequestParam String cliente,
                                 @RequestParam String placa,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        try {
            vehicleService.addVehicle(cliente, placa);
            redirectAttributes.addFlashAttribute("success", "Vehículo registrado correctamente.");
            return "redirect:/modulo";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/registrar";
        }
    }

    @GetMapping("/modulo")
    public String modulo(HttpSession session, Model model, @RequestParam(required = false) String search) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        List<Vehicle> vehicles = vehicleService.findBySearch(search);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("search", search == null ? "" : search);
        return "modulo";
    }

    @GetMapping("/pago/{id}")
    public String pago(@PathVariable String id,
                       @RequestParam(required = false) Long paymentId,
                       HttpSession session,
                       Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        Vehicle vehicle = vehicleService.findById(id);
        if (vehicle == null) {
            return "redirect:/modulo";
        }
        BigDecimal total = parkingService.calculateCost(vehicle.getMinutos());
        Pago payment = null;
        DetallePago detail = null;
        if (paymentId != null) {
            Optional<Pago> savedPayment = pagoService.findByIdAndVehicleId(paymentId, vehicle.getId());
            if (savedPayment.isEmpty()) {
                return "redirect:/modulo";
            }
            payment = savedPayment.get();
            detail = pagoService.findDetailByPaymentId(payment.getId())
                    .orElseThrow(() -> new IllegalStateException("El pago no tiene un detalle de boleta asociado."));
        }
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("total", total);
        model.addAttribute("totalText", parkingService.formatCurrency(total));
        model.addAttribute("payment", payment);
        model.addAttribute("detail", detail);
        model.addAttribute("receiptTotalText", payment == null
                ? parkingService.formatCurrency(total)
                : parkingService.formatCurrency(payment.getMonto()));
        model.addAttribute("receiptTimeText", detail == null
                ? parkingService.formatMinutes(vehicle.getMinutos())
                : parkingService.formatMinutes(detail.getMinutosEstacionado()));
        return "pago";
    }

    @PostMapping("/pago/{id}/confirmar")
    public String confirmarPago(@PathVariable String id,
                                @RequestParam String metodoPago,
                                @RequestParam(required = false) BigDecimal montoRecibido,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        Vehicle vehicle = vehicleService.findById(id);
        if (vehicle == null) {
            return "redirect:/modulo";
        }
        try {
            Pago payment = pagoService.registrarPago(vehicle.getId(), metodoPago, montoRecibido);
            redirectAttributes.addAttribute("paymentId", payment.getId());
            return "redirect:/pago/" + vehicle.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/pago/" + vehicle.getId();
        }
    }

    @PostMapping("/modulo")
    public String guardarVehiculoDesdeModal(@RequestParam String id,
                                           @RequestParam String cliente,
                                           @RequestParam String placa,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        try {
            vehicleService.updateVehicle(id, cliente, placa);
            redirectAttributes.addFlashAttribute("success", "Datos actualizados correctamente.");
            return "redirect:/modulo";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/modulo";
        }
    }

    @PostMapping("/pago/{id}/finalizar")
    public String finalizarPago(@PathVariable String id, HttpSession session) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        Vehicle vehicle = vehicleService.findById(id);
        if (vehicle == null || !pagoService.hasPaymentForVehicle(vehicle.getId())) {
            return "redirect:/pago/" + id;
        }
        // Mantiene el vehiculo por la clave foranea; los listados activos lo excluyen por su pago.
        return "redirect:/modulo";
    }

    @GetMapping("/datos")
    public String datos(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/login";
        }
        model.addAttribute("espaciosData", List.of(
                java.util.Map.of("name", "Disponibles", "value", 32),
                java.util.Map.of("name", "Ocupados", "value", 68)
        ));
        model.addAttribute("tiempoSemanaData", parkingService.buildWeeklyStayData());
        model.addAttribute("vehiculosDiaData", parkingService.buildDailyVehicleData());
        model.addAttribute("ingresosData", parkingService.buildMonthlyIncomeData());
        model.addAttribute("annualIncomeTotal", parkingService.getAnnualIncomeTotal());
        return "datos";
    }
}
