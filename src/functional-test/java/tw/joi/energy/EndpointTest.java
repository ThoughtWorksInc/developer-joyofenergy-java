package tw.joi.energy;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import tw.joi.energy.config.ElectricityReadingsGenerator;
import tw.joi.energy.controller.MeterReadingManager;
import tw.joi.energy.controller.StoreReadingsRequest;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.repository.SmartMeterRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
public class EndpointTest {
    private static final String DEFAULT_METER_ID = "id";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldStoreReadings() {
        var meterRepository = new SmartMeterRepository();
        var smartMeterReadingsManager = new MeterReadingManager(meterRepository);
        var meterReadings = new ElectricityReadingsGenerator().generate(5);

        smartMeterReadingsManager.storeReadings(DEFAULT_METER_ID, meterReadings);

        assertThat(meterRepository.findById(DEFAULT_METER_ID).get().electricityReadings())
                .isEqualTo(meterReadings);
    }

    @SuppressWarnings("DataFlowIssue")
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

    @Disabled("needs to be migrated off the REST interface")
    @Test
    public void shouldCalculateAllPrices() {
        String smartMeterId = "bob";
        List<ElectricityReading> data = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        populateReadingsForMeter(smartMeterId, data);

        ResponseEntity<CompareAllResponse> response =
                restTemplate.getForEntity("/price-plans/compare-all/" + smartMeterId, CompareAllResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isEqualTo(new CompareAllResponse(
                        Map.of("price-plan-0", 200, "price-plan-1", 40, "price-plan-2", 20), null));
    }

    @Disabled("needs to be migrated off the REST interface")
    @SuppressWarnings("rawtypes")
    @Test
    public void givenMeterIdAndLimitShouldReturnRecommendedCheapestPricePlans() {
        String smartMeterId = "jane";
        List<ElectricityReading> data = List.of(
                new ElectricityReading(Instant.parse("2024-04-26T00:00:10.00Z"), new BigDecimal(10)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:20.00Z"), new BigDecimal(20)),
                new ElectricityReading(Instant.parse("2024-04-26T00:00:30.00Z"), new BigDecimal(30)));
        populateReadingsForMeter(smartMeterId, data);

        ResponseEntity<Map[]> response =
                restTemplate.getForEntity("/price-plans/recommend/" + smartMeterId + "?limit=2", Map[].class);

        assertThat(response.getBody()).containsExactly(Map.of("price-plan-2", 20), Map.of("price-plan-1", 40));
    }

    private static HttpEntity<StoreReadingsRequest> toHttpEntity(StoreReadingsRequest meterReadings) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(meterReadings, headers);
    }

    private void populateReadingsForMeter(String smartMeterId, List<ElectricityReading> data) {
        StoreReadingsRequest readings = new StoreReadingsRequest(smartMeterId, data);

        HttpEntity<StoreReadingsRequest> entity = toHttpEntity(readings);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/readings", entity, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    record CompareAllResponse(Map<String, Integer> pricePlanComparisons, String pricePlanId) {}
}
