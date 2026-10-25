package com.smartpark.dao;

import com.smartpark.model.DetallePago;

import java.util.List;
import java.util.Optional;

/** Contrato de acceso a datos del detalle histórico de cada pago. */
public interface DetallePagoDao {

    DetallePago save(DetallePago detalle);

    Optional<DetallePago> findByPaymentId(Long paymentId);

    /** Todos los detalles de pago, del más reciente al más antiguo. */
    List<DetallePago> findAllLatestFirst();
}
