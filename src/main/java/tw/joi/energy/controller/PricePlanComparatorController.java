package tw.joi.energy.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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
import tw.joi.energy.repository.AccountRepository;
import tw.joi.energy.service.PricePlanService;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

  public static final String PRICE_PLAN_ID_KEY = "pricePlanId";
  public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
  private final PricePlanService pricePlanService;
  private final AccountRepository accountService;

  public PricePlanComparatorController(
      PricePlanService pricePlanService, AccountRepository accountRepository) {
    this.pricePlanService = pricePlanService;
    this.accountService = accountRepository;
  }

  @GetMapping("/compare-all/{smartMeterId}")
  public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(
      @PathVariable String smartMeterId) {
    String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
    Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
        pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

    if (!consumptionsForPricePlans.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Map<String, Object> pricePlanComparisons = new HashMap<>();
    pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
    pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans.get());

    return consumptionsForPricePlans.isPresent()
        ? ResponseEntity.ok(pricePlanComparisons)
        : ResponseEntity.notFound().build();
  }

  @GetMapping("/recommend/{smartMeterId}")
  public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
      @PathVariable String smartMeterId,
      @RequestParam(value = "limit", required = false) Integer limit) {
    Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
        pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

    if (!consumptionsForPricePlans.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    List<Map.Entry<String, BigDecimal>> recommendations =
        new ArrayList<>(consumptionsForPricePlans.get().entrySet());
    recommendations.sort(Comparator.comparing(Map.Entry::getValue));

    if (limit != null && limit < recommendations.size()) {
      recommendations = recommendations.subList(0, limit);
    }

    return ResponseEntity.ok(recommendations);
  }
}
