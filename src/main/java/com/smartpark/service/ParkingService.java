package com.smartpark.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParkingService {
    private static final BigDecimal RATE_PER_MINUTE = new BigDecimal("0.10");

    public BigDecimal calculateCost(int minutes) {
        return RATE_PER_MINUTE.multiply(BigDecimal.valueOf(minutes)).setScale(2, RoundingMode.HALF_UP);
    }

    public String formatCurrency(BigDecimal amount) {
        return String.format("S/ %.2f", amount.setScale(2, RoundingMode.HALF_UP));
    }

    public String formatMinutes(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        if (hours == 0) {
            return minutes + " min";
        }
        if (minutes == 0) {
            return hours + " h";
        }
        return hours + " h " + minutes + " min";
    }

    public List<Map<String, Object>> buildWeeklyStayData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(new LinkedHashMap<>() {{ put("semana", "Semana 1"); put("minutos", 80); }});
        data.add(new LinkedHashMap<>() {{ put("semana", "Semana 2"); put("minutos", 95); }});
        data.add(new LinkedHashMap<>() {{ put("semana", "Semana 3"); put("minutos", 75); }});
        data.add(new LinkedHashMap<>() {{ put("semana", "Semana 4"); put("minutos", 100); }});
        return data;
    }

    public List<Map<String, Object>> buildDailyVehicleData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(new LinkedHashMap<>() {{ put("dia", "Lun"); put("vehiculos", 65); }});
        data.add(new LinkedHashMap<>() {{ put("dia", "Mar"); put("vehiculos", 72); }});
        data.add(new LinkedHashMap<>() {{ put("dia", "Mié"); put("vehiculos", 80); }});
        data.add(new LinkedHashMap<>() {{ put("dia", "Jue"); put("vehiculos", 76); }});
        data.add(new LinkedHashMap<>() {{ put("dia", "Vie"); put("vehiculos", 92); }});
        data.add(new LinkedHashMap<>() {{ put("dia", "Sáb"); put("vehiculos", 95); }});
        data.add(new LinkedHashMap<>() {{ put("dia", "Dom"); put("vehiculos", 70); }});
        return data;
    }

    public List<Map<String, Object>> buildMonthlyIncomeData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(new LinkedHashMap<>() {{ put("mes", "Ene"); put("monto", 4200); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "Feb"); put("monto", 4500); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "Mar"); put("monto", 4800); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "Abr"); put("monto", 5100); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "May"); put("monto", 5300); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "Jun"); put("monto", 5600); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "Jul"); put("monto", 5900); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "Ago"); put("monto", 6100); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "Sep"); put("monto", 5800); }});
        data.add(new LinkedHashMap<>() {{ put("mes", "Oct"); put("monto", 6200); }});
        return data;
    }

    public int getAnnualIncomeTotal() {
        return buildMonthlyIncomeData().stream()
                .mapToInt(item -> ((Number) item.get("monto")).intValue())
                .sum();
    }
}
