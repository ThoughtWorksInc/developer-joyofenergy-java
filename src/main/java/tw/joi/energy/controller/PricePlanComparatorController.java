package tw.joi.energy.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.SmartMeterRepository;
import tw.joi.energy.service.PricePlanService;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

    public static final String PRICE_PLAN_ID_KEY = "pricePlanId";
    public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanService pricePlanService;
    private final SmartMeterRepository smartMeterRepository;

    public PricePlanComparatorController(PricePlanService pricePlanService, SmartMeterRepository smartMeterRepository) {
        this.pricePlanService = pricePlanService;
        this.smartMeterRepository = smartMeterRepository;
    }

    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(smartMeterId);
        if (optionalSmartMeter.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        SmartMeter smartMeter = optionalSmartMeter.get();

        String pricePlanId = smartMeter.getPricePlanId();
        Map<String, BigDecimal> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeter);

        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans);
        return ResponseEntity.ok(pricePlanComparisons);
    }

    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
            @PathVariable String smartMeterId, @RequestParam(value = "limit", required = false) Integer limit) {
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(smartMeterId);
        if (optionalSmartMeter.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        SmartMeter smartMeter = optionalSmartMeter.get();
        Map<String, BigDecimal> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeter);

        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(consumptionsForPricePlans.entrySet());
        recommendations.sort(Map.Entry.comparingByValue());

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }
        return ResponseEntity.ok(recommendations);
    }
}
