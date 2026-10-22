package com.smartpark.service;

import com.smartpark.model.DetallePago;
import com.smartpark.model.Pago;
import com.smartpark.model.Vehicle;
import com.smartpark.repository.DetallePagoRepository;
import com.smartpark.repository.PagoRepository;
import com.smartpark.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PagoService {
    private static final long INITIAL_PAYMENT_NUMBER = 576450L;
    private static final Pattern PAYMENT_NUMBER_PATTERN = Pattern.compile("\\d+");

    private final PagoRepository pagoRepository;
    private final DetallePagoRepository detallePagoRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingService parkingService;

    public PagoService(PagoRepository pagoRepository,
                       DetallePagoRepository detallePagoRepository,
                       VehicleRepository vehicleRepository,
                       ParkingService parkingService) {
        this.pagoRepository = pagoRepository;
        this.detallePagoRepository = detallePagoRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingService = parkingService;
    }

    // Registra el pago y su detalle histórico en una sola transacción.
    @Transactional
    public Pago registrarPago(Long vehicleId, String metodoPago, BigDecimal montoRecibido) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado."));
        String paymentMethod = normalizePaymentMethod(metodoPago);
        BigDecimal total = parkingService.calculateCost(vehicle.getMinutos());

        if ("Efectivo".equals(paymentMethod)
                && (montoRecibido == null || montoRecibido.compareTo(total) < 0)) {
            throw new IllegalArgumentException("El monto recibido no cubre el total a pagar.");
        }

        if (!pagoRepository.acquirePaymentCodeLock()) {
            throw new IllegalStateException("No se pudo bloquear la generación del código de pago.");
        }
        String paymentCode = generatePaymentCode(LocalDate.now().getYear());
        LocalDateTime paymentTime = LocalDateTime.now();

        Pago pago = new Pago();
        pago.setCodigoPago(paymentCode);
        pago.setVehicle(vehicle);
        pago.setMonto(total);
        pago.setMetodoPago(paymentMethod);
        pago.setFechaPago(paymentTime);
        pago.setEstado("PAGADO");
        Pago savedPayment = pagoRepository.saveAndFlush(pago);

        DetallePago detalle = new DetallePago();
        detalle.setPago(savedPayment);
        detalle.setCliente(vehicle.getCliente());
        detalle.setPlaca(vehicle.getPlaca());
        detalle.setHoraIngreso(vehicle.getHoraIngreso());
        detalle.setHoraSalida(paymentTime);
        detalle.setMinutosEstacionado(vehicle.getMinutos());
        detalle.setTotal(total);
        detallePagoRepository.save(detalle);

        return savedPayment;
    }

    // Busca un pago y verifica que pertenezca al vehículo de la ruta.
    @Transactional(readOnly = true)
    public Optional<Pago> findByIdAndVehicleId(Long paymentId, Long vehicleId) {
        return pagoRepository.findByIdAndVehicle_Id(paymentId, vehicleId);
    }

    // Consulta el detalle histórico asociado al identificador del pago.
    @Transactional(readOnly = true)
    public Optional<DetallePago> findDetailByPaymentId(Long paymentId) {
        return detallePagoRepository.findById(paymentId);
    }

    // Comprueba si el vehículo ya tiene un pago que lo conserva en el historial.
    @Transactional(readOnly = true)
    public boolean hasPaymentForVehicle(Long vehicleId) {
        return pagoRepository.existsByVehicle_Id(vehicleId);
    }

    // Obtiene el siguiente código correlativo mientras conserva el bloqueo transaccional.
    private String generatePaymentCode(int year) {
        String prefix = String.format(Locale.ROOT, "SP-%04d-", year);
        List<String> existingCodes = pagoRepository.findPaymentCodesByPrefix(prefix);
        long nextNumber;
        if (existingCodes.isEmpty()) {
            nextNumber = INITIAL_PAYMENT_NUMBER;
        } else {
            long highestNumber = existingCodes.stream()
                .mapToLong(code -> parsePaymentNumber(code, prefix))
                .max()
                .orElseThrow();
            nextNumber = Math.addExact(highestNumber, 1);
        }
        return prefix + String.format(Locale.ROOT, "%06d", nextNumber);
    }

    // Valida y convierte el sufijo numérico de un código de pago existente.
    private long parsePaymentNumber(String paymentCode, String prefix) {
        String suffix = paymentCode.substring(prefix.length());
        if (!PAYMENT_NUMBER_PATTERN.matcher(suffix).matches()) {
            throw new IllegalStateException("Existe un código de pago con formato inválido: " + paymentCode);
        }
        try {
            return Long.parseLong(suffix);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("El número de un código de pago excede el límite permitido.", ex);
        }
    }

    // Limita los métodos persistidos a las opciones que ofrece la interfaz.
    private String normalizePaymentMethod(String method) {
        if (method == null) {
            throw new IllegalArgumentException("Selecciona un método de pago válido.");
        }
        return switch (method.trim().toLowerCase(Locale.ROOT)) {
            case "efectivo" -> "Efectivo";
            case "yape" -> "Yape";
            case "tarjeta" -> "Tarjeta";
            default -> throw new IllegalArgumentException("Selecciona un método de pago válido.");
        };
    }
}
