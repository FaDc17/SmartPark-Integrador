package com.smartpark.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalle_pago")
public class DetallePago {

    @Id
    @Column(name = "pago_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    @Column(name = "cliente", nullable = false, length = 150)
    private String cliente;

    @Column(name = "placa", nullable = false, length = 20)
    private String placa;

    @Column(name = "hora_ingreso", nullable = false)
    private LocalDateTime horaIngreso;

    @Column(name = "hora_salida", nullable = false)
    private LocalDateTime horaSalida;

    @Column(name = "minutos_estacionado", nullable = false)
    private int minutosEstacionado;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    public DetallePago() {
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setHoraIngreso(LocalDateTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public void setMinutosEstacionado(int minutosEstacionado) {
        this.minutosEstacionado = minutosEstacionado;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public Pago getPago() {
        return pago;
    }

    public String getCliente() {
        return cliente;
    }

    public String getPlaca() {
        return placa;
    }

    public LocalDateTime getHoraIngreso() {
        return horaIngreso;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public int getMinutosEstacionado() {
        return minutosEstacionado;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
