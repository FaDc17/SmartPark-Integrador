package com.smartpark.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente", nullable = false)
    private String cliente;

    @Column(name = "placa", nullable = false)
    private String placa;

    @Column(name = "hora_ingreso", nullable = false)
    private LocalDateTime horaIngreso;

    @Column(name = "minutos")
    private int minutos;

    public Vehicle() {
    }

    public Vehicle(String cliente, String placa, int minutos) {
        this.cliente = cliente;
        this.placa = placa;
        this.minutos = minutos;
    }

    @PrePersist
    protected void registrarHoraIngreso() {
        if (this.horaIngreso == null) {
            this.horaIngreso = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public LocalDateTime getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(LocalDateTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }
}