package com.smartpark.repository;

import com.smartpark.model.DetallePago;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetallePagoRepository extends JpaRepository<DetallePago, Long> {

    /** Historial completo: los pagos más recientes primero. */
    // Carga el pago junto al detalle para mostrar código, método y fecha sin consultas extra.
    @EntityGraph(attributePaths = "pago")
    List<DetallePago> findAllByOrderByIdDesc();
}