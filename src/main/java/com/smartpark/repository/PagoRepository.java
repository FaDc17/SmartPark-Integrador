package com.smartpark.repository;

import com.smartpark.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query(value = "SELECT pg_advisory_xact_lock(7342198162501::bigint) IS NOT NULL", nativeQuery = true)
    boolean acquirePaymentCodeLock();

    @Query(value = "SELECT codigo_pago FROM pagos WHERE codigo_pago LIKE CONCAT(:prefix, '%')", nativeQuery = true)
    List<String> findPaymentCodesByPrefix(@Param("prefix") String prefix);

    Optional<Pago> findByIdAndVehicle_Id(Long id, Long vehicleId);

    boolean existsByVehicle_Id(Long vehicleId);

    Optional<Pago> findFirstByVehicle_IdOrderByIdDesc(Long vehicleId);
}
