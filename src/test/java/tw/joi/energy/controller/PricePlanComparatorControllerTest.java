package tw.joi.energy.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.domain.PricePlan;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.SmartMeterRepository;
import tw.joi.energy.service.PricePlanService;

public class PricePlanComparatorControllerTest {
    private static final String WORST_PLAN_ID = "worst-supplier";
    private static final String BEST_PLAN_ID = "best-supplier";
    private static final String SECOND_BEST_PLAN_ID = "second-best-supplier";
    private static final String SMART_METER_ID = "smart-meter-id";
    private PricePlanComparator controller;
    private SmartMeterRepository smartMeterRepository;
    private PricePlan WORST_PLAN;

    @BeforeEach
    public void setUp() {
        WORST_PLAN = new PricePlan(WORST_PLAN_ID, null, BigDecimal.TEN);
        PricePlan pricePlan2 = new PricePlan(BEST_PLAN_ID, null, BigDecimal.ONE);
        PricePlan pricePlan3 = new PricePlan(SECOND_BEST_PLAN_ID, null, BigDecimal.valueOf(2));
        List<PricePlan> pricePlans = List.of(WORST_PLAN, pricePlan2, pricePlan3);
        PricePlanService pricePlanService = new PricePlanService(pricePlans);

        smartMeterRepository = new SmartMeterRepository();
        controller = new PricePlanComparator(pricePlanService, smartMeterRepository);
    }

    @Test
    public void calculatedCostForEachPricePlan_happyPath() {
        List<ElectricityReading> readings = List.of(
                new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(5.0)),
                new ElectricityReading(Instant.now(), BigDecimal.valueOf(15.0)));
        var smartMeter = new SmartMeter(WORST_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = controller.calculatedCostForEachPricePlan(SMART_METER_ID);

        Map<String, Object> expected = Map.of(
                PricePlanComparator.PRICE_PLAN_ID_KEY,
                WORST_PLAN_ID,
                PricePlanComparator.PRICE_PLAN_COMPARISONS_KEY,
                Map.of(
                        WORST_PLAN_ID, BigDecimal.valueOf(100.0),
                        BEST_PLAN_ID, BigDecimal.valueOf(10.0),
                        SECOND_BEST_PLAN_ID, BigDecimal.valueOf(20.0)));
        assertThat(response).isEqualTo(expected);
    }

    @Test
    public void calculatedCostForEachPricePlan_noReadings() {
        assertThatThrownBy(() -> controller.calculatedCostForEachPricePlan("not-found"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("args");
    }

    @Test
    public void recommendCheapestPricePlans_noLimit() {
        var readings = List.of(
                new ElectricityReading(Instant.now().minusSeconds(1800), BigDecimal.valueOf(3.0)),
                new ElectricityReading(Instant.now(), BigDecimal.valueOf(35.0)));
        var smartMeter = new SmartMeter(WORST_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = controller.recommendCheapestPricePlans(SMART_METER_ID, null);

        var expectedPricePlanToCost = List.of(
                new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(32.0)),
                new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(64.0)),
                new AbstractMap.SimpleEntry<>(WORST_PLAN_ID, BigDecimal.valueOf(320.0)));
        assertThat(response).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void recommendCheapestPricePlans_withLimit() {
        var readings = List.of(
                new ElectricityReading(Instant.now().minusSeconds(2700), BigDecimal.valueOf(5.0)),
                new ElectricityReading(Instant.now(), BigDecimal.valueOf(20.0)));
        var smartMeter = new SmartMeter(WORST_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = controller.recommendCheapestPricePlans(SMART_METER_ID, 2);

        var expectedPricePlanToCost = List.of(
                new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(15.0)),
                new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(30.0)));
        assertThat(response).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void recommendCheapestPricePlans_limitHigherThanNumberOfEntries() {
        var readings = List.of(
                new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(3.0)),
                new ElectricityReading(Instant.now(), BigDecimal.valueOf(25.0)));
        var smartMeter = new SmartMeter(WORST_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = controller.recommendCheapestPricePlans(SMART_METER_ID, 5);

        var expectedPricePlanToCost = List.of(
                new AbstractMap.SimpleEntry<>(BEST_PLAN_ID, BigDecimal.valueOf(22.0)),
                new AbstractMap.SimpleEntry<>(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(44.0)),
                new AbstractMap.SimpleEntry<>(WORST_PLAN_ID, BigDecimal.valueOf(220.0)));
        assertThat(response).isEqualTo(expectedPricePlanToCost);
    }
}
