package com.smartpark.model;

public class Vehicle {
    private String id;
    private String cliente;
    private String placa;
    private String horaIngreso;
    private int minutos;

    public Vehicle() {
    }

    public Vehicle(String id, String cliente, String placa, String horaIngreso, int minutos) {
        this.id = id;
        this.cliente = cliente;
        this.placa = placa;
        this.horaIngreso = horaIngreso;
        this.minutos = minutos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(String horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }
}
