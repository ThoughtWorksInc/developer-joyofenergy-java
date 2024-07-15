package tw.joi.energy.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static tw.joi.energy.fixture.ElectricityReadingFixture.createReading;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.repository.SmartMeterRepository;

public class MeterReadingManagerTest {

    private static final String SMART_METER_ID = "10101010";
    private final SmartMeterRepository smartMeterRepository = new SmartMeterRepository();
    private final MeterReadingManager meterReadingManager = new MeterReadingManager(smartMeterRepository);

    @Test
    @DisplayName("storeReadings should throw exception given a null meterId")
    public void store_readings_should_throw_exception_given_meter_id_is_null() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(null, emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    @DisplayName("storeReadings should throw exception given meterId is empty string")
    public void store_readings_should_throw_exception_given_meter_id_is_empty() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings("", emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    @DisplayName("storeReadings should throw exception given readings is null")
    public void store_readings_should_throw_exception_given_readings_is_null() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    @DisplayName("storeReadings should throw exception given readings is emtpy list")
    public void store_readings_should_throw_exception_given_readings_is_empty() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    @DisplayName("storeReadings should succeed given non-empty list of readings")
    public void store_readings_should_succeed_given_meter_readings() {
        var readingsToStore = List.of(createReading(LocalDate.now(), 1.0));

        meterReadingManager.storeReadings(SMART_METER_ID, readingsToStore);

        var storedReadings = smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings();
        assertThat(storedReadings).isEqualTo(readingsToStore);
    }

    @Test
    @DisplayName("storeReadings should succeed when called multiple times")
    public void store_readings_should_succeed_given_multiple_batches_of_meter_readings() {
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
    @DisplayName("storeReadings should write supplied readings to correct meter")
    public void store_readings_should_store_to_correct_meter_given_multiple_meters_exist() {
        var meterReadings = List.of(createReading(LocalDate.now(), 1.0));
        var otherMeterReadings = List.of(createReading(LocalDate.now(), 2.0));

        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        meterReadingManager.storeReadings("00001", otherMeterReadings);

        assertThat(smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings())
                .isEqualTo(meterReadings);
    }

    @Test
    @DisplayName("readReadings should throw exception if supplied meterId is not persisted")
    public void read_readings_should_throw_exception_given_meter_id_is_not_persisted() {
        assertThatThrownBy(() -> meterReadingManager.readReadings(SMART_METER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    @DisplayName("readReadings should return previously supplied readings for a known meterId")
    public void read_readings_should_return_readings_given_readings_are_existent() {
        // given
        var meterReadings = List.of(createReading(LocalDate.now(), 1.0));
        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        // expect
        assertThat(meterReadingManager.readReadings(SMART_METER_ID)).isEqualTo(meterReadings);
    }
}
