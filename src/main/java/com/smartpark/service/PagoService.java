package com.smartpark.service;

import com.smartpark.model.DetallePago;
import com.smartpark.model.Pago;
import com.smartpark.model.Vehicle;
import com.smartpark.payment.MetodoPagoStrategy;
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
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PagoService {
    private static final long INITIAL_PAYMENT_NUMBER = 576450L;
    private static final Pattern PAYMENT_NUMBER_PATTERN = Pattern.compile("\\d+");

    private final PagoRepository pagoRepository;
    private final DetallePagoRepository detallePagoRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingService parkingService;
    private final Map<String, MetodoPagoStrategy> metodos;

    public PagoService(PagoRepository pagoRepository,
                       DetallePagoRepository detallePagoRepository,
                       VehicleRepository vehicleRepository,
                       ParkingService parkingService,
                       List<MetodoPagoStrategy> estrategias) {
        this.pagoRepository = pagoRepository;
        this.detallePagoRepository = detallePagoRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingService = parkingService;
        this.metodos = estrategias.stream().collect(Collectors.toMap(
                e -> e.getNombre().toLowerCase(Locale.ROOT), e -> e));
    }

    // Registra el pago y su detalle histórico en una sola transacción.
    // Es idempotente por estadía: si el vehículo ya tiene su pago (reenvío del formulario,
    // doble clic o botón Atrás), devuelve ese mismo pago en vez de registrar otro.
    // Una nueva estadía del mismo cliente se registra como un vehículo nuevo y paga normalmente.
    @Transactional
    public Pago registrarPago(Long vehicleId, String metodoPago, BigDecimal montoRecibido) {
        // El bloqueo de la fila obliga a que dos envíos simultáneos se procesen uno tras otro.
        Vehicle vehicle = vehicleRepository.findByIdForUpdate(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado."));
        Optional<Pago> existingPayment = pagoRepository.findFirstByVehicle_IdOrderByIdDesc(vehicleId);
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        // Patrón Strategy: cada método de pago valida sus propias reglas.
        MetodoPagoStrategy metodo = resolverMetodo(metodoPago);
        BigDecimal total = parkingService.calculateCost(vehicle.getMinutos());
        metodo.validar(total, montoRecibido);

        if (!pagoRepository.acquirePaymentCodeLock()) {
            throw new IllegalStateException("No se pudo bloquear la generación del código de pago.");
        }
        String paymentCode = generatePaymentCode(LocalDate.now().getYear());
        LocalDateTime paymentTime = LocalDateTime.now();

        Pago pago = new Pago();
        pago.setCodigoPago(paymentCode);
        pago.setVehicle(vehicle);
        pago.setMonto(total);
        pago.setMetodoPago(metodo.getNombre());
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
        long highestNumber = existingCodes.stream()
                .mapToLong(code -> parsePaymentNumber(code, prefix))
                .max()
                .orElse(INITIAL_PAYMENT_NUMBER - 1);
        long nextNumber = Math.addExact(highestNumber, 1);
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

    // Selecciona la estrategia de pago según el método elegido en la interfaz.
    private MetodoPagoStrategy resolverMetodo(String metodo) {
        if (metodo == null) {
            throw new IllegalArgumentException("Selecciona un método de pago válido.");
        }
        MetodoPagoStrategy estrategia = metodos.get(metodo.trim().toLowerCase(Locale.ROOT));
        if (estrategia == null) {
            throw new IllegalArgumentException("Selecciona un método de pago válido.");
        }
        return estrategia;
    }
}