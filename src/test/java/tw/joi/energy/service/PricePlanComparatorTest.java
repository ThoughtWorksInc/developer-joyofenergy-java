package tw.joi.energy.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static tw.joi.energy.fixture.PricePlanFixture.BEST_PLAN_ID;
import static tw.joi.energy.fixture.PricePlanFixture.BEST_PRICE_PLAN;
import static tw.joi.energy.fixture.PricePlanFixture.DEFAULT_PRICE_PLAN;
import static tw.joi.energy.fixture.PricePlanFixture.SECOND_BEST_PLAN_ID;
import static tw.joi.energy.fixture.PricePlanFixture.WORST_PLAN_ID;
import static tw.joi.energy.fixture.PricePlanFixture.WORST_PRICE_PLAN;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.domain.PricePlan;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.PricePlanRepository;
import tw.joi.energy.repository.SmartMeterRepository;

class PricePlanComparatorTest {
    private static final String SMART_METER_ID = "smart-meter-id";
    private PricePlanComparator comparator;
    private SmartMeterRepository smartMeterRepository;

    private static final ZoneId GMT = ZoneId.of("GMT");
    private static final Instant testInstant = Instant.ofEpochSecond(1721124813L);
    private static final Clock today = Clock.fixed(testInstant, GMT);
    private static final Clock tenDaysAgo = Clock.fixed(testInstant.minus(10, ChronoUnit.DAYS), GMT);

    @BeforeEach
    void setUp() {
        List<PricePlan> pricePlans = List.of(WORST_PRICE_PLAN, BEST_PRICE_PLAN, DEFAULT_PRICE_PLAN);
        PricePlanRepository pricePlanRepository = new PricePlanRepository(pricePlans);

        smartMeterRepository = new SmartMeterRepository();
        comparator = new PricePlanComparator(pricePlanRepository, smartMeterRepository);
    }

    @Test
    @DisplayName("recommend should return costs for all plans when no limit specified")
    void recommendShouldReturnCostsForAllPlansWhenNoLimitSpecified() {
        var readings = List.of(new ElectricityReading(tenDaysAgo, 3.0), new ElectricityReading(today, 35.0));
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
    @DisplayName("recommend should return top two cheapest costings if limit of 2 supplied ")
    void recommendShouldReturnTopTwoCheapestCostingsIfLimitOf2Supplied() {
        var readings = List.of(new ElectricityReading(tenDaysAgo, 5.0), new ElectricityReading(today, 20.0));
        var smartMeter = new SmartMeter(WORST_PRICE_PLAN, readings);
        smartMeterRepository.save(SMART_METER_ID, smartMeter);

        var response = comparator.recommendCheapestPricePlans(SMART_METER_ID, 2);

        var expectedPricePlanToCost = List.of(
                Map.entry(BEST_PLAN_ID, BigDecimal.valueOf(15.0)),
                Map.entry(SECOND_BEST_PLAN_ID, BigDecimal.valueOf(30.0)));
        assertThat(response).isEqualTo(expectedPricePlanToCost);
    }

    @Test
    @DisplayName("recommend should return all costs if limit is larger than sum of known price plans")
    void recommendShouldReturnAllCostsIfLimitIsLargerThanSumOfKnownPricePlans() {
        var readings = List.of(new ElectricityReading(tenDaysAgo, 3.0), new ElectricityReading(today, 25.0));
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
    @DisplayName("recommend should throw exception given a missing smartId")
    void recommendShouldThrowExceptionGivenMissingSmartId() {
        assertThatThrownBy(() -> comparator.recommendCheapestPricePlans("not_existent_id", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("missing args");
    }
}
