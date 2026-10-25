package com.smartpark.dao;

import com.smartpark.model.Pago;

import java.util.List;
import java.util.Optional;

/** Contrato de acceso a datos de pagos. */
public interface PagoDao {

    /** Guarda el pago y lo sincroniza de inmediato con la base de datos. */
    Pago saveAndFlush(Pago pago);

    Optional<Pago> findByIdAndVehicleId(Long paymentId, Long vehicleId);

    Optional<Pago> findLatestByVehicleId(Long vehicleId);

    boolean existsByVehicleId(Long vehicleId);

    /** Códigos de pago que comienzan con el prefijo indicado (por ejemplo "SP-2026-"). */
    List<String> findPaymentCodesByPrefix(String prefix);

    /** Toma el bloqueo transaccional que serializa la generación de códigos de pago. */
    boolean acquirePaymentCodeLock();
}
