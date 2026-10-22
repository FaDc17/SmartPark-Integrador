package com.smartpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.smartpark.model.Vehicle;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByPlaca(String placa);

    @Query("SELECT v FROM Vehicle v WHERE NOT EXISTS "
            + "(SELECT p.id FROM Pago p WHERE p.vehicle.id = v.id) ORDER BY v.id ASC")
    List<Vehicle> findActiveVehicles();

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE NOT EXISTS "
            + "(SELECT p.id FROM Pago p WHERE p.vehicle.id = v.id)")
    long countActiveVehicles();
}
