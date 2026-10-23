package com.smartpark.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.smartpark.model.Vehicle;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByPlaca(String placa);

    // Bloquea la fila del vehículo hasta el fin de la transacción para serializar pagos simultáneos.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id")
    Optional<Vehicle> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT v FROM Vehicle v WHERE NOT EXISTS "
            + "(SELECT p.id FROM Pago p WHERE p.vehicle.id = v.id) ORDER BY v.id ASC")
    List<Vehicle> findActiveVehicles();

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE NOT EXISTS "
            + "(SELECT p.id FROM Pago p WHERE p.vehicle.id = v.id)")
    long countActiveVehicles();
}
