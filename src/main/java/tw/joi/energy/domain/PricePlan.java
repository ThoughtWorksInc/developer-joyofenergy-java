package tw.joi.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PricePlan {

    private final String energySupplier;
    private final String planName;
    private final BigDecimal unitRate; // unit price per kWh
    private final Map<DayOfWeek, BigDecimal> peakTimeMultipliers;

    public PricePlan(String planName, String energySupplier, BigDecimal unitRate) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = Collections.emptyMap();
    }

    public PricePlan(
            String planName,
            String energySupplier,
            BigDecimal unitRate,
            Map<DayOfWeek, BigDecimal> peakTimeMultipliers) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = Collections.unmodifiableMap(new HashMap<>(peakTimeMultipliers));
    }

    public String getEnergySupplier() {
        return energySupplier;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public BigDecimal getPrice(ZonedDateTime dateTime) {
        return unitRate;
    }

    @Override
    public String toString() {
        return "Name: '" + planName + "', Unit Rate: " + unitRate + ", Supplier: '" + energySupplier + "'";
    }
}
