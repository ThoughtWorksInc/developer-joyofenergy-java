package tw.joi.energy.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.domain.PricePlan;
import tw.joi.energy.domain.SmartMeter;

@Service
public class PricePlanService {
    private final List<PricePlan> pricePlans;

    public PricePlanService(List<PricePlan> pricePlans) {
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
                .min(Comparator.comparing(ElectricityReading::time))
                .get();
        var latest = electricityReadings.stream()
                .max(Comparator.comparing(ElectricityReading::time))
                .get();

        BigDecimal energyConsumed = latest.reading().subtract(oldest.reading());
        return energyConsumed.multiply(pricePlan.getUnitRate());
    }
}
