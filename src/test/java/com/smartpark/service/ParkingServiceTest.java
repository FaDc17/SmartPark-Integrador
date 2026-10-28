package com.smartpark.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParkingServiceTest {

    private final ParkingService service = new ParkingService();
    private Locale previousLocale;

    @BeforeEach
    void fixLocale() {
        // formatCurrency usa el locale por defecto; se fija para que el test sea determinista.
        previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(previousLocale);
    }

    @Test
    @DisplayName("Happy: calculateCost multiplica tarifa por minutos")
    void calculateCost() {
        assertEquals(new BigDecimal("6.00"), service.calculateCost(60));
        assertEquals(new BigDecimal("0.10"), service.calculateCost(1));
        assertEquals(new BigDecimal("8.50"), service.calculateCost(85));
    }

    @Test
    @DisplayName("Borde: cero minutos cuesta 0.00")
    void calculateCostZero() {
        assertEquals(new BigDecimal("0.00"), service.calculateCost(0));
    }

    @Test
    @DisplayName("Unhappy: minutos negativos producen costo negativo (sin validación)")
    void calculateCostNegative() {
        assertEquals(new BigDecimal("-1.00"), service.calculateCost(-10));
    }

    @Test
    void formatCurrency() {
        assertEquals("S/ 6.00", service.formatCurrency(new BigDecimal("6")));
        assertEquals("S/ 1.24", service.formatCurrency(new BigDecimal("1.235")));
    }

    @Test
    @DisplayName("Unhappy: formatCurrency con null lanza NullPointerException")
    void formatCurrencyNull() {
        assertThrows(NullPointerException.class, () -> service.formatCurrency(null));
    }

    @Test
    @DisplayName("formatMinutes: solo minutos, solo horas, horas y minutos")
    void formatMinutes() {
        assertEquals("45 min", service.formatMinutes(45));
        assertEquals("0 min", service.formatMinutes(0));
        assertEquals("2 h", service.formatMinutes(120));
        assertEquals("1 h 25 min", service.formatMinutes(85));
    }

    @Test
    void weeklyStayData() {
        List<Map<String, Object>> data = service.buildWeeklyStayData();
        assertEquals(4, data.size());
        assertEquals("Semana 1", data.get(0).get("semana"));
        assertEquals(80, data.get(0).get("minutos"));
        assertEquals(100, data.get(3).get("minutos"));
    }

    @Test
    void dailyVehicleData() {
        List<Map<String, Object>> data = service.buildDailyVehicleData();
        assertEquals(7, data.size());
        assertEquals("Lun", data.get(0).get("dia"));
        assertEquals(95, data.get(5).get("vehiculos"));
        assertEquals("Dom", data.get(6).get("dia"));
    }

    @Test
    void monthlyIncomeData() {
        List<Map<String, Object>> data = service.buildMonthlyIncomeData();
        assertEquals(10, data.size());
        assertEquals("Ene", data.get(0).get("mes"));
        assertEquals(6200, data.get(9).get("monto"));
    }

    @Test
    @DisplayName("getAnnualIncomeTotal suma los 10 meses")
    void annualIncomeTotal() {
        assertEquals(53500, service.getAnnualIncomeTotal());
    }
}
