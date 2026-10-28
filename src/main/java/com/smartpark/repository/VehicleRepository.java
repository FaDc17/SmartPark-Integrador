package com.smartpark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smartpark.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
        // Comprueba si ya existe un vehículo con la placa indicada.
        boolean existsByPlaca(String placa);
        // Obtiene los vehículos que todavía no tienen un pago asociado.
        @Query("SELECT v FROM Vehicle v WHERE NOT EXISTS "
                + "(SELECT p.id FROM Pago p WHERE p.vehicle.id = v.id) ORDER BY v.id ASC")
        List<Vehicle> findActiveVehicles();
        // Cuenta los vehículos que todavía no tienen un pago asociado.
        @Query("SELECT COUNT(v) FROM Vehicle v WHERE NOT EXISTS "
                + "(SELECT p.id FROM Pago p WHERE p.vehicle.id = v.id)")
        long countActiveVehicles();
}
