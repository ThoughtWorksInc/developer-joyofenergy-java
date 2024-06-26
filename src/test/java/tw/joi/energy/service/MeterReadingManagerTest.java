package tw.joi.energy.service;

import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.repository.SmartMeterRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tw.joi.energy.fixture.ElectricityReadingFixture.createReading;

public class MeterReadingManagerTest {

    private static final String SMART_METER_ID = "10101010";
    private final SmartMeterRepository smartMeterRepository = new SmartMeterRepository();
    private final MeterReadingManager meterReadingManager = new MeterReadingManager(smartMeterRepository);

    @Test
    public void should_throw_exception_when_store_readings_given_meter_id_is_null() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(null, emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    public void should_throw_exception_when_store_readings_given_meter_id_is_empty() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings("", emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    public void should_throw_exception_when_store_readings_given_readings_is_null() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    public void should_throw_exception_when_store_readings_given_readings_is_empty() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    public void should_store_readings_successfully_when_store_readings_given_collection_of_meter_readings() {
        var readingsToStore = List.of(createReading(LocalDate.now(), 1.0));

        meterReadingManager.storeReadings(SMART_METER_ID, readingsToStore);

        var storedReadings = smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings();
        assertThat(storedReadings).isEqualTo(readingsToStore);
    }

    @Test
    public void should_store_readings_successfully_when_store_readings_given_multiple_batches_of_meter_readings() {
        var meterReadings = List.of(createReading(LocalDate.now(), 1.0));
        var otherMeterReadings = List.of(createReading(LocalDate.now(), 2.0));

        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        meterReadingManager.storeReadings(SMART_METER_ID, otherMeterReadings);

        List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
        expectedElectricityReadings.addAll(meterReadings);
        expectedElectricityReadings.addAll(otherMeterReadings);

        assertThat(smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings())
                .isEqualTo(expectedElectricityReadings);
    }

    @Test
    public void
            should_store_readings_to_associated_smart_meter_when_store_reading_given_meter_readings_associated_to_different_smart_meters() {
        var meterReadings = List.of(createReading(LocalDate.now(), 1.0));
        var otherMeterReadings = List.of(createReading(LocalDate.now(), 2.0));

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

    @Test
    public void should_return_readings_when_read_readings_given_readings_are_existent() {
        // given
        var meterReadings = List.of(createReading(LocalDate.now(), 1.0));
        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        // expect
        assertThat(meterReadingManager.readReadings(SMART_METER_ID)).isEqualTo(meterReadings);
    }
}
