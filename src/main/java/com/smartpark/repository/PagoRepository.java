package com.smartpark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartpark.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    // Adquiere un bloqueo de PostgreSQL para evitar códigos de pago repetidos simultáneamente.
    @Query(value = "SELECT pg_advisory_xact_lock(7342198162501::bigint) IS NOT NULL", nativeQuery = true)
    boolean acquirePaymentCodeLock();
    // Busca códigos de pago existentes que comiencen con el prefijo indicado.
    @Query(value = "SELECT codigo_pago FROM pagos WHERE codigo_pago LIKE CONCAT(:prefix, '%')", nativeQuery = true)
    List<String> findPaymentCodesByPrefix(@Param("prefix") String prefix);
    // Busca un pago por su ID confirmando que pertenezca al vehículo indicado.
    Optional<Pago> findByIdAndVehicle_Id(Long id, Long vehicleId);
    // Comprueba si el vehículo ya tiene un pago registrado.
    boolean existsByVehicle_Id(Long vehicleId);
}
