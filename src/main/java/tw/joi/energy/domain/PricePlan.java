package tw.joi.energy.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PricePlan {

    private final String energySupplier;
    private final String planName;
    private final BigDecimal unitRate; // unit price per kWh

    public PricePlan(String planName, String energySupplier, BigDecimal unitRate) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
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

    public BigDecimal getPrice(LocalDateTime dateTime) {
        return unitRate;
    }

    @Override
    public String toString() {
        return "Name: '" + planName + "', Unit Rate: " + unitRate + ", Supplier: '" + energySupplier + "'";
    }
}
