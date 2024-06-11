package tw.joi.energy.domain;

import java.io.*;
import java.math.BigDecimal;
import java.text.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public class PricePlan {

    private String energySupplier;
    private String planName;
    private final BigDecimal unitRate; // unit price per kWh
    private final List<PeakTimeMultiplier> peakTimeMultipliers;

    public PricePlan(
            String planName, String energySupplier, BigDecimal unitRate, List<PeakTimeMultiplier> peakTimeMultipliers) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = peakTimeMultipliers;
    }

    public String getEnergySupplier() {
        return energySupplier;
    }

    public void setEnergySupplier(String supplierName) {
        this.energySupplier = supplierName;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String name) {
        this.planName = name;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {
        return unitRate;
    }

    @Override
    public String toString() {
        return "Name: '" + planName + "', Unit Rate: " + unitRate + ", Supplier: '" + energySupplier + "'";
    }

    static class PeakTimeMultiplier {

        DayOfWeek dayOfWeek;
        BigDecimal multiplier;

        public PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.multiplier = multiplier;
        }
    }
}
