package com.smartpark.service;

import com.smartpark.model.Vehicle;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class VehicleService {
    private static final int CAPACITY = 100;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private int nextIndex = 6;

    public VehicleService() {
        vehicles.add(new Vehicle("001", "Carlos Mendoza", "ABC-123", "08:35", 85));
        vehicles.add(new Vehicle("002", "María Torres", "XYZ-456", "10:20", 45));
        vehicles.add(new Vehicle("003", "José Ramos", "BCD-789", "07:50", 130));
        vehicles.add(new Vehicle("004", "Ana Gutiérrez", "DEF-321", "13:45", 20));
        vehicles.add(new Vehicle("005", "Luis Castillo", "GHI-654", "09:10", 185));
    }

    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles);
    }

    public List<Vehicle> findBySearch(String query) {
        String text = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (text.isEmpty()) {
            return findAll();
        }
        return vehicles.stream()
                .filter(v ->
                        v.getCliente().toLowerCase(Locale.ROOT).contains(text)
                                || v.getPlaca().toLowerCase(Locale.ROOT).contains(text)
                                || v.getId().contains(text))
                .toList();
    }

    public Vehicle addVehicle(String cliente, String placa) {
        String customer = cliente == null ? "" : cliente.trim();
        String plate = placa == null ? "" : placa.trim().toUpperCase(Locale.ROOT);

        if (customer.isEmpty() || plate.isEmpty()) {
            throw new IllegalArgumentException("Ingresa el nombre del cliente y la placa del vehículo.");
        }

        nextIndex += 1;
        Vehicle vehicle = new Vehicle(String.format("%03d", nextIndex), customer, plate, randomHour(), 1);
        vehicles.add(vehicle);
        return vehicle;
    }

    public Vehicle updateVehicle(String id, String cliente, String placa) {
        Vehicle vehicle = vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado."));

        vehicle.setCliente(cliente == null ? vehicle.getCliente() : cliente.trim());
        vehicle.setPlaca(placa == null ? vehicle.getPlaca() : placa.trim().toUpperCase(Locale.ROOT));
        return vehicle;
    }

    public Vehicle findById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void removeVehicle(String id) {
        vehicles.removeIf(v -> v.getId().equals(id));
    }

    public int getOccupiedSpaces() {
        return vehicles.size();
    }

    public int getFreeSpaces() {
        return Math.max(0, CAPACITY - getOccupiedSpaces());
    }

    public double getWeeklyUsePercentage() {
        double weeklyUsed = 550.0;
        return Math.round((weeklyUsed / 700.0) * 10000.0) / 100.0;
    }

    public String randomHour() {
        Random random = new Random();
        int hour = 7 + random.nextInt(11);
        int minute = random.nextInt(60);
        return String.format("%02d:%02d", hour, minute);
    }
}
