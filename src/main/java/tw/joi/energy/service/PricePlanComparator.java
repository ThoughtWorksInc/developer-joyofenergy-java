package tw.joi.energy.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.PricePlanRepository;
import tw.joi.energy.repository.SmartMeterRepository;

public class PricePlanComparator {

    private final PricePlanRepository pricePlanRepository;
    private final SmartMeterRepository smartMeterRepository;

    public PricePlanComparator(PricePlanRepository ppr, SmartMeterRepository smr) {
        this.pricePlanRepository = ppr;
        this.smartMeterRepository = smr;
    }

    public List<Map.Entry<String, BigDecimal>> recommendCheapestPricePlans(String smartMeterId, Integer limit) {
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(smartMeterId);
        if (!optionalSmartMeter.isPresent()) {
            throw new RuntimeException("missing args");
        }
        SmartMeter smartMeter = optionalSmartMeter.get();
        Map<String, BigDecimal> consumptionsForPricePlans =
                pricePlanRepository.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeter);

        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(consumptionsForPricePlans.entrySet());
        recommendations.sort(Map.Entry.comparingByValue());

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }
        return recommendations;
    }
}
