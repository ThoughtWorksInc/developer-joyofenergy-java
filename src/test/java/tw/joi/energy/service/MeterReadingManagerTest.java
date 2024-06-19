package tw.joi.energy.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
    public void should_throw_exception_when_store_readings_given_no_meter_id_is_supplied() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(null, Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @ParameterizedTest(name = "should_throw_exception_when_store_readings_given_readings_are_{0}")
    @NullAndEmptySource
    public void should_throw_exception_when_store_readings_given_no_readings_are_supplied(
            List<ElectricityReading> electricityReadings) {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, electricityReadings))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    public void should_store_readings_successfully_when_store_readings_given_multiple_batches_of_meter_readings() {
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
    public void should_store_readings_to_associated_smart_meter_when_store_reading_given_meter_readings_associated_to_different_smart_meters() {
        var meterReadings = ElectricityReadingsGenerator.generate(5);
        var otherMeterReadings = ElectricityReadingsGenerator.generate(5);

        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        meterReadingManager.storeReadings("00001", otherMeterReadings);

        assertThat(smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings())
                .isEqualTo(meterReadings);
    }

    @Test
    public void should_throw_exception_when_read_readings_given_meter_id_is_not_existent() {
        assertThatThrownBy(() -> meterReadingManager.readReadings(SMART_METER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }
}
