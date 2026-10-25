package com.smartpark.dao;

import com.smartpark.model.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos de vehículos. Los servicios dependen de esta interfaz
 * y no conocen la tecnología de persistencia que hay detrás.
 */
public interface VehicleDao {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findById(Long id);

    /** Busca el vehículo bloqueando su fila hasta el fin de la transacción. */
    Optional<Vehicle> findByIdForUpdate(Long id);

    /** Vehículos que todavía no tienen un pago registrado (siguen estacionados). */
    List<Vehicle> findActive();

    long countActive();

    boolean existsByPlaca(String placa);

    void delete(Vehicle vehicle);
}
