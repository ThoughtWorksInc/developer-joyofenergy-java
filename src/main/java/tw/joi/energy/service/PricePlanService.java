package tw.joi.energy.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.domain.PricePlan;
import tw.joi.energy.repository.MeterReadingRepository;

@Service
public class PricePlanService {

    private final List<PricePlan> pricePlans;
    private final MeterReadingRepository meterReadingRepository;

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingRepository meterReadingRepository) {
        this.pricePlans = pricePlans;
        this.meterReadingRepository = meterReadingRepository;
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(
            String smartMeterId) {
        var electricityReadings = meterReadingRepository.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream()
                .collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));
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
