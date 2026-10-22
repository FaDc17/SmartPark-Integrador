package com.smartpark.service;

import com.smartpark.model.Vehicle;
import com.smartpark.repository.VehicleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class VehicleService {
    private static final int CAPACITY = 100;
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // Carga una sola vez los vehículos demostrativos que aún no existan en PostgreSQL.
    @PostConstruct
    public void ensureDemoVehicles() {
        saveDemoVehicleIfMissing("Carlos Mendoza", "ABC-123", 85, 8, 35);
        saveDemoVehicleIfMissing("María Torres", "XYZ-456", 45, 10, 20);
        saveDemoVehicleIfMissing("José Ramos", "BCD-789", 130, 7, 50);
        saveDemoVehicleIfMissing("Ana Gutiérrez", "DEF-321", 20, 13, 45);
        saveDemoVehicleIfMissing("Luis Castillo", "GHI-654", 185, 9, 10);
    }

    private void saveDemoVehicleIfMissing(String cliente, String placa, int minutos, int hora, int minuto) {
        if (!vehicleRepository.existsByPlaca(placa)) {
            Vehicle vehicle = new Vehicle(cliente, placa, minutos);
            vehicle.setHoraIngreso(LocalDate.now().atTime(hora, minuto));
            vehicleRepository.save(vehicle);
        }
    }

    // Consulta todos los vehículos actualmente estacionados en PostgreSQL.
    public List<Vehicle> findAll() {
        return vehicleRepository.findActiveVehicles();
    }

    // Busca en los vehículos persistidos por cliente, placa o identificador.
    public List<Vehicle> findBySearch(String query) {
        String text = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (text.isEmpty()) {
            return findAll();
        }
        return vehicleRepository.findAll().stream()
                .filter(v ->
                        v.getCliente().toLowerCase(Locale.ROOT).contains(text)
                                || v.getPlaca().toLowerCase(Locale.ROOT).contains(text)
                                || v.getId().toString().contains(text))
                .toList();
    }

    // Valida y registra un vehículo nuevo; PostgreSQL genera su identificador.
    public Vehicle addVehicle(String cliente, String placa) {
        String customer = cliente == null ? "" : cliente.trim();
        String plate = placa == null ? "" : placa.trim().toUpperCase(Locale.ROOT);

        if (customer.isEmpty() || plate.isEmpty()) {
            throw new IllegalArgumentException("Ingresa el nombre del cliente y la placa del vehículo.");
        }

        return vehicleRepository.save(new Vehicle(customer, plate, 1));
    }

    // Actualiza los datos del vehículo persistido sin cambiar su identificador.
    public Vehicle updateVehicle(String id, String cliente, String placa) {
        Vehicle vehicle = findById(id);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehículo no encontrado.");
        }

        vehicle.setCliente(cliente == null ? vehicle.getCliente() : cliente.trim());
        vehicle.setPlaca(placa == null ? vehicle.getPlaca() : placa.trim().toUpperCase(Locale.ROOT));
        return vehicleRepository.save(vehicle);
    }

    // Busca un vehículo persistido por su ID numérico recibido desde la ruta.
    public Vehicle findById(String id) {
        try {
            return vehicleRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    // Elimina de PostgreSQL el vehículo indicado por su ID.
    public void removeVehicle(String id) {
        Vehicle vehicle = findById(id);
        if (vehicle != null) {
            vehicleRepository.delete(vehicle);
        }
    }

    // Cuenta los vehículos persistidos para calcular los espacios ocupados.
    public int getOccupiedSpaces() {
        return Math.toIntExact(vehicleRepository.countActiveVehicles());
    }

    // Calcula los espacios libres según la capacidad total del estacionamiento.
    public int getFreeSpaces() {
        return Math.max(0, CAPACITY - getOccupiedSpaces());
    }

    // Calcula el porcentaje demostrativo de uso semanal de la capacidad.
    public double getWeeklyUsePercentage() {
        double weeklyUsed = 550.0;
        return Math.round((weeklyUsed / 700.0) * 10000.0) / 100.0;
    }

    // Genera una hora de ingreso demostrativa dentro del horario de operación.
    public String randomHour() {
        Random random = new Random();
        int hour = 7 + random.nextInt(11);
        int minute = random.nextInt(60);
        return String.format("%02d:%02d", hour, minute);
    }
}
