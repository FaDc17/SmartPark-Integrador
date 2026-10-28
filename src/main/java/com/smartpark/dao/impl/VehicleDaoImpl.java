package com.smartpark.dao.impl;

import com.smartpark.dao.VehicleDao;
import com.smartpark.model.Vehicle;
import com.smartpark.repository.VehicleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VehicleDaoImpl implements VehicleDao {

    private final VehicleRepository repository;

    public VehicleDaoImpl(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return repository.save(vehicle);
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Vehicle> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id);
    }

    @Override
    public List<Vehicle> findActive() {
        return repository.findActiveVehicles();
    }

    @Override
    public long countActive() {
        return repository.countActiveVehicles();
    }

    @Override
    public boolean existsByPlaca(String placa) {
        return repository.existsByPlaca(placa);
    }

    @Override
    public void delete(Vehicle vehicle) {
        repository.delete(vehicle);
    }
}
