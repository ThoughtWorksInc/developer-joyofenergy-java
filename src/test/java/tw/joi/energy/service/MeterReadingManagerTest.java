package tw.joi.energy.service;

import org.junit.jupiter.api.Test;
import tw.joi.energy.config.ElectricityReadingsGenerator;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.repository.SmartMeterRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MeterReadingManagerTest {

    private static final String SMART_METER_ID = "10101010";
    private final SmartMeterRepository smartMeterRepository = new SmartMeterRepository();
    private final MeterReadingManager meterReadingManager = new MeterReadingManager(smartMeterRepository);

    @Test
    public void given_no_meter_id_is_supplied_when_storing_should_throw_exception() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(null, Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    public void given_empty_meter_reading_should_return_error_response() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    public void given_null_readings_are_supplied_when_storing_should_return_error_response() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    public void given_multiple_batches_of_meter_readings_should_store() {
        var meterReadings = ElectricityReadingsGenerator.generate(5);
        var otherMeterReadings = ElectricityReadingsGenerator.generate(5);

        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        meterReadingManager.storeReadings(SMART_METER_ID, otherMeterReadings);

        List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
        expectedElectricityReadings.addAll(meterReadings);
        expectedElectricityReadings.addAll(otherMeterReadings);

        assertThat(smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings())
                .isEqualTo(expectedElectricityReadings);
    }

    @Test
    public void given_meter_readings_associated_with_the_user_should_store_associated_with_user() {
        var meterReadings = ElectricityReadingsGenerator.generate(5);
        var otherMeterReadings = ElectricityReadingsGenerator.generate(5);

        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        meterReadingManager.storeReadings("00001", otherMeterReadings);

        assertThat(smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings())
                .isEqualTo(meterReadings);
    }

    @Test
    public void given_meter_id_that_is_not_recognised_should_return_not_found() {
        assertThatThrownBy(() -> meterReadingManager.readReadings(SMART_METER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }
}
