package tw.joi.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.domain.PricePlan;
import tw.joi.energy.repository.AccountRepository;
import tw.joi.energy.repository.MeterReadingRepository;
import tw.joi.energy.service.PricePlanService;

public class PricePlanComparatorControllerTest {
    private static final String WORST_PLAN_ID = "worst-supplier";
    private static final String BEST_PLAN_ID = "best-supplier";
    private static final String SECOND_BEST_PLAN_ID = "second-best-supplier";
    private static final String SMART_METER_ID = "smart-meter-id";
    private PricePlanComparatorController controller;
    private MeterReadingRepository meterReadingRepository;
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        meterReadingRepository = new MeterReadingRepository(new HashMap<>());

        PricePlan pricePlan1 = new PricePlan(WORST_PLAN_ID, null, BigDecimal.TEN);
        PricePlan pricePlan2 = new PricePlan(BEST_PLAN_ID, null, BigDecimal.ONE);
        PricePlan pricePlan3 = new PricePlan(SECOND_BEST_PLAN_ID, null, BigDecimal.valueOf(2));
        List<PricePlan> pricePlans = List.of(pricePlan1, pricePlan2, pricePlan3);
        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingRepository);

        accountRepository = new AccountRepository(Map.of(SMART_METER_ID, WORST_PLAN_ID));

        controller = new PricePlanComparatorController(pricePlanService, accountRepository);
    }

    @Test
    public void calculatedCostForEachPricePlan_happyPath() {
        var electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(5.0));
        var otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(15.0));
        meterReadingRepository.storeReadings(SMART_METER_ID, List.of(electricityReading, otherReading));

        ResponseEntity<Map<String, Object>> response = controller.calculatedCostForEachPricePlan(SMART_METER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> expected = Map.of(
                PricePlanComparatorController.PRICE_PLAN_ID_KEY,
                WORST_PLAN_ID,
                PricePlanComparatorController.PRICE_PLAN_COMPARISONS_KEY,
                Map.of(
                        WORST_PLAN_ID, BigDecimal.valueOf(100.0),
                        BEST_PLAN_ID, BigDecimal.valueOf(10.0),
                        SECOND_BEST_PLAN_ID, BigDecimal.valueOf(20.0)));
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void calculatedCostForEachPricePlan_noReadings() {
        ResponseEntity<Map<String, Object>> response = controller.calculatedCostForEachPricePlan("not-found");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void recommendCheapestPricePlans_noLimit() {
        var electricityReading = new ElectricityReading(Instant.now().minusSeconds(1800), BigDecimal.valueOf(3.0));
        var otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(35.0));
        meterReadingRepository.storeReadings(SMART_METER_ID, List.of(electricityReading, otherReading));

        ResponseEntity<List<Map.Entry<String, BigDecimal>>> response =
                controller.recommendCheapestPricePlans(SMART_METER_ID, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var expectedPricePlanToCost = List.of(
                new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(32.0)),
                new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(64.0)),
                new AbstractMap.SimpleEntry<>(WORST_PLAN_ID, BigDecimal.valueOf(320.0)));
        assertThat(response.getBody()).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void recommendCheapestPricePlans_withLimit() {
        var electricityReading = new ElectricityReading(Instant.now().minusSeconds(2700), BigDecimal.valueOf(5.0));
        var otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(20.0));
        meterReadingRepository.storeReadings(SMART_METER_ID, List.of(electricityReading, otherReading));

        ResponseEntity<List<Map.Entry<String, BigDecimal>>> response =
                controller.recommendCheapestPricePlans(SMART_METER_ID, 2);

        var expectedPricePlanToCost = List.of(
                new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(15.0)),
                new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(30.0)));
        assertThat(response.getBody()).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void recommendCheapestPricePlans_limitHigherThanNumberOfEntries() {
        var reading0 = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(3.0));
        var reading1 = new ElectricityReading(Instant.now(), BigDecimal.valueOf(25.0));
        meterReadingRepository.storeReadings(SMART_METER_ID, List.of(reading0, reading1));

        ResponseEntity<List<Map.Entry<String, BigDecimal>>> response =
                controller.recommendCheapestPricePlans(SMART_METER_ID, 5);

        var expectedPricePlanToCost = List.of(
                new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(22.0)),
                new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(44.0)),
                new AbstractMap.SimpleEntry<>(WORST_PLAN_ID, BigDecimal.valueOf(220.0)));
        assertThat(response.getBody()).isEqualTo(expectedPricePlanToCost);
    }
}
