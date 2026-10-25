package com.smartpark.dao.impl;

import com.smartpark.dao.PagoDao;
import com.smartpark.model.Pago;
import com.smartpark.repository.PagoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PagoDaoImpl implements PagoDao {

    private final PagoRepository repository;

    public PagoDaoImpl(PagoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pago saveAndFlush(Pago pago) {
        return repository.saveAndFlush(pago);
    }

    @Override
    public Optional<Pago> findByIdAndVehicleId(Long paymentId, Long vehicleId) {
        return repository.findByIdAndVehicle_Id(paymentId, vehicleId);
    }

    @Override
    public Optional<Pago> findLatestByVehicleId(Long vehicleId) {
        return repository.findFirstByVehicle_IdOrderByIdDesc(vehicleId);
    }

    @Override
    public boolean existsByVehicleId(Long vehicleId) {
        return repository.existsByVehicle_Id(vehicleId);
    }

    @Override
    public List<String> findPaymentCodesByPrefix(String prefix) {
        return repository.findPaymentCodesByPrefix(prefix);
    }

    @Override
    public boolean acquirePaymentCodeLock() {
        return repository.acquirePaymentCodeLock();
    }
}
