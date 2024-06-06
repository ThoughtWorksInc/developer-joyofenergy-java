package tw.joi.energy.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.SmartMeterRepository;
import tw.joi.energy.service.PricePlanService;

public class PricePlanComparator {

    public static final String PRICE_PLAN_ID_KEY = "pricePlanId";
    public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanService pricePlanService;
    private final SmartMeterRepository smartMeterRepository;

    public PricePlanComparator(PricePlanService pricePlanService, SmartMeterRepository smartMeterRepository) {
        this.pricePlanService = pricePlanService;
        this.smartMeterRepository = smartMeterRepository;
    }

    public Map<String, Object> calculatedCostForEachPricePlan(String smartMeterId) {
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(smartMeterId);
        if (optionalSmartMeter.isEmpty()) {
            throw new RuntimeException("missing args");
        }
        SmartMeter smartMeter = optionalSmartMeter.get();

        String pricePlanId = smartMeter.getPricePlanId();
        Map<String, BigDecimal> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeter);

        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans);
        return pricePlanComparisons;
    }

    public List<Map.Entry<String, BigDecimal>> recommendCheapestPricePlans(String smartMeterId, Integer limit) {
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(smartMeterId);
        if (optionalSmartMeter.isEmpty()) {
            throw new RuntimeException("missing args");
        }
        SmartMeter smartMeter = optionalSmartMeter.get();
        Map<String, BigDecimal> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeter);

        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(consumptionsForPricePlans.entrySet());
        recommendations.sort(Map.Entry.comparingByValue());

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }
        return recommendations;
    }
}
