package com.smartpark.dao.impl;

import com.smartpark.dao.DetallePagoDao;
import com.smartpark.model.DetallePago;
import com.smartpark.repository.DetallePagoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DetallePagoDaoImpl implements DetallePagoDao {

    private final DetallePagoRepository repository;

    public DetallePagoDaoImpl(DetallePagoRepository repository) {
        this.repository = repository;
    }

    @Override
    public DetallePago save(DetallePago detalle) {
        return repository.save(detalle);
    }

    @Override
    public Optional<DetallePago> findByPaymentId(Long paymentId) {
        return repository.findById(paymentId);
    }

    @Override
    public List<DetallePago> findAllLatestFirst() {
        return repository.findAllByOrderByIdDesc();
    }
}
