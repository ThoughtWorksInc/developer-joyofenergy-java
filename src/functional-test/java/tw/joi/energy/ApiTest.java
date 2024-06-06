package tw.joi.energy;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tw.joi.energy.config.ElectricityReadingsGenerator;
import tw.joi.energy.service.MeterReadingManager;
import tw.joi.energy.service.PricePlanComparator;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.repository.SmartMeterRepository;
import tw.joi.energy.config.TestData;

public class ApiTest {
    private static final String DEFAULT_METER_ID = "id";

    @Test
    public void shouldStoreReadings() {
        var meterRepository = new SmartMeterRepository();
        var smartMeterReadingsManager = new MeterReadingManager(meterRepository);
        var meterReadings = new ElectricityReadingsGenerator().generate(5);

        smartMeterReadingsManager.storeReadings(DEFAULT_METER_ID, meterReadings);

        assertThat(meterRepository.findById(DEFAULT_METER_ID).get().electricityReadings())
                .isEqualTo(meterReadings);
    }

    @Test
    public void givenMeterIdShouldReturnAMeterReadingAssociatedWithMeterId() {
        var meterRepository = new SmartMeterRepository();
        var smartMeterReadingsManager = new MeterReadingManager(meterRepository);
        String smartMeterId = "alice";
        var meterReadings = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        smartMeterReadingsManager.storeReadings(smartMeterId, meterReadings);

        var actualReadings = smartMeterReadingsManager.readReadings(smartMeterId);

        assertThat(actualReadings).isEqualTo(meterReadings);
    }

    @Test
    public void shouldCalculateAllPrices() {
        String smartMeterId = "bob";
        List<ElectricityReading> data = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        var meterRepository = TestData.smartMeterRepository();
        var smartMeterReadingsManager = new MeterReadingManager(meterRepository);
        smartMeterReadingsManager.storeReadings(smartMeterId, data);

        var comparator = new PricePlanComparator(TestData.pricePlanService(), meterRepository);
        HashMap<String, Object> response =
                (HashMap<String, Object>) comparator.calculatedCostForEachPricePlan(smartMeterId);

        var expected = new HashMap<String, Object>(Map.of(
                "pricePlanComparisons",
                Map.of(
                        "price-plan-0", BigDecimal.valueOf(200),
                        "price-plan-1", BigDecimal.valueOf(40),
                        "price-plan-2", BigDecimal.valueOf(20))));
        expected.put("pricePlanId", null);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    public void givenMeterIdAndLimitShouldReturnRecommendedCheapestPricePlans() {
        String smartMeterId = "jane";
        List<ElectricityReading> data = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        var meterRepository = TestData.smartMeterRepository();
        var smartMeterReadingsManager = new MeterReadingManager(meterRepository);
        smartMeterReadingsManager.storeReadings(smartMeterId, data);

        var comparator = new PricePlanComparator(TestData.pricePlanService(), meterRepository);
        var recommendation = comparator.recommendCheapestPricePlans(smartMeterId, 2);

        assertThat(recommendation)
                .containsExactly(
                        Map.entry("price-plan-2", BigDecimal.valueOf(20)),
                        Map.entry("price-plan-1", BigDecimal.valueOf(40)));
    }
}
