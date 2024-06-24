package tw.joi.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.domain.PricePlan;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.PricePlanRepository;
import tw.joi.energy.repository.SmartMeterRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static tw.joi.energy.fixture.ElectricityReadingFixture.createReading;
import static tw.joi.energy.fixture.PricePlanFixture.*;

public class PricePlanComparatorTest {
    private static final String SMART_METER_ID = "smart-meter-id";
    private PricePlanComparator comparator;
    private SmartMeterRepository smartMeterRepository;
    private final LocalDateTime today = LocalDateTime.now();
    private final LocalDateTime tenDaysAgo = today.minusDays(10);

    @BeforeEach
    public void setUp() {
        List<PricePlan> pricePlans = List.of(WORST_PRICE_PLAN, BEST_PRICE_PLAN, DEFAULT_PRICE_PLAN);
        PricePlanRepository pricePlanRepository = new PricePlanRepository(pricePlans);

        smartMeterRepository = new SmartMeterRepository();
        comparator = new PricePlanComparator(pricePlanRepository, smartMeterRepository);
    }

    @Test
    public void should_return_all_price_plans_costs_when_calculated_cost_for_each_price_plan_given_readings_and_price_plans() {
        List<ElectricityReading> readings = List.of(
                createReading(tenDaysAgo, 5.0),
                createReading(today, 15.0));
        var smartMeter = new SmartMeter(WORST_PRICE_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = comparator.calculatedCostForEachPricePlan(SMART_METER_ID);

        Map<String, Object> expected = Map.of(
                "currentlyAssignedPricePlanId",
                WORST_PLAN_ID,
                PricePlanComparator.PRICE_PLAN_COMPARISONS_KEY,
                Map.of(
                        WORST_PLAN_ID, BigDecimal.valueOf(100.0),
                        BEST_PLAN_ID, BigDecimal.valueOf(10.0),
                        SECOND_BEST_PLAN_ID, BigDecimal.valueOf(20.0)));
        assertThat(response).isEqualTo(expected);
    }

    @Test
    public void should_throw_exception_when_calculated_cost_for_each_price_plan_given_no_readings() {
        assertThatThrownBy(() -> comparator.calculatedCostForEachPricePlan("not-found"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("args");
    }

    @Test
    public void should_return_all_costs_when_recommend_cheapest_price_plans_given_no_limit() {
        var readings = List.of(
                createReading(tenDaysAgo, 3.0),
                createReading(today, 35.0));
        var smartMeter = new SmartMeter(WORST_PRICE_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = comparator.recommendCheapestPricePlans(SMART_METER_ID, null);

        var expectedPricePlanToCost = List.of(
                Map.entry(BEST_PLAN_ID, BigDecimal.valueOf(32.0)),
                Map.entry(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(64.0)),
                Map.entry(WORST_PLAN_ID, BigDecimal.valueOf(320.0)));
        assertThat(response).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void should_return_top_2_cheapest_costs_when_recommend_cheapest_price_plans_given_limit_is_2() {
        var readings = List.of(
                createReading(tenDaysAgo, 5.0),
                createReading(today, 20.0));
        var smartMeter = new SmartMeter(WORST_PRICE_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = comparator.recommendCheapestPricePlans(SMART_METER_ID, 2);

        var expectedPricePlanToCost = List.of(
                Map.entry(BEST_PLAN_ID, BigDecimal.valueOf(15.0)),
                Map.entry(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(30.0)));
        assertThat(response).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void should_return_all_costs_when_recommend_cheapest_price_plans_given_limit_is_bigger_than_count_of_price_plans() {
        var readings = List.of(
                createReading(tenDaysAgo, 3.0),
                createReading(today, 25.0));
        var smartMeter = new SmartMeter(WORST_PRICE_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = comparator.recommendCheapestPricePlans(SMART_METER_ID, 5);

        var expectedPricePlanToCost = List.of(
                Map.entry(BEST_PLAN_ID, BigDecimal.valueOf(22.0)),
                Map.entry(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(44.0)),
                Map.entry(WORST_PLAN_ID, BigDecimal.valueOf(220.0)));
        assertThat(response).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    public void should_throw_exception_when_recommend_cheapest_price_plans_given_smart_meter_is_not_existent() {
        assertThatThrownBy(() -> comparator.recommendCheapestPricePlans("not_existent_id", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("missing args");
    }
}
