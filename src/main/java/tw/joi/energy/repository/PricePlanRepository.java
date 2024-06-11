package tw.joi.energy.repository;

import static java.util.Comparator.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.domain.PricePlan;
import tw.joi.energy.domain.SmartMeter;

public class PricePlanRepository {
    private final List<PricePlan> pricePlans;

    public PricePlanRepository(List<PricePlan> pricePlans) {
        this.pricePlans = pricePlans;
    }

    public Map<String, BigDecimal> getConsumptionCostOfElectricityReadingsForEachPricePlan(SmartMeter smartMeter) {
        var electricityReadings = smartMeter.electricityReadings();

        return pricePlans.stream()
                .collect(Collectors.toMap(
                        PricePlan::getPlanName, pricePlan -> calculateCost(electricityReadings, pricePlan)));
    }

    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        var oldest = electricityReadings.stream()
                .min(comparing(ElectricityReading::time))
                .get();
        var latest = electricityReadings.stream()
                .max(comparing(ElectricityReading::time))
                .get();

        BigDecimal energyConsumed = latest.reading().subtract(oldest.reading());
        return energyConsumed.multiply(pricePlan.getPrice(LocalDateTime.now()));
    }

    public List<PricePlan> getAllPricePlans() {
        return pricePlans;
    }
}
