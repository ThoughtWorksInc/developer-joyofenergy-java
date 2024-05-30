package tw.joi.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public class PricePlan {

  private final String energySupplier;
  private final String planName;
  private final BigDecimal unitRate; // unit price per kWh

  public PricePlan(
      String planName,
      String energySupplier,
      BigDecimal unitRate) {
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
}
