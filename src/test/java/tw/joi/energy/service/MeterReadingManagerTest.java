package tw.joi.energy.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.repository.SmartMeterRepository;

class MeterReadingManagerTest {

    private static final ZoneId GMT = ZoneId.of("GMT");
    private static final String SMART_METER_ID = "10101010";
    private static final long ARBITRARY_TIME_STAMP = 1721124813L;
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.ofEpochSecond(ARBITRARY_TIME_STAMP), GMT);
    private final SmartMeterRepository smartMeterRepository = new SmartMeterRepository();
    private final MeterReadingManager meterReadingManager = new MeterReadingManager(smartMeterRepository);

    @Test
    @DisplayName("storeReadings should throw exception given a null meterId")
    public void storeReadingsShouldThrowExceptionGivenNullMeterId() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(null, emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    @DisplayName("storeReadings should throw exception given meterId is empty string")
    void storeReadingsShouldThrowExceptionGivenEmptyMeterId() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings("", emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    @DisplayName("storeReadings should throw exception given readings is null")
    void storeReadingsShouldThrowExceptionGivenNullReadings() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    @DisplayName("storeReadings should throw exception given readings is emtpy list")
    void storeReadingsShouldThrowExceptionGivenEmptyReadings() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    @DisplayName("storeReadings should succeed given non-empty list of readings")
    void storeReadingsShouldSucceedGivenNonEmptyReadings() {
        var readingsToStore = List.of(new ElectricityReading(FIXED_CLOCK, 1.0));

        meterReadingManager.storeReadings(SMART_METER_ID, readingsToStore);

        var storedReadings = smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings();
        assertThat(storedReadings).isEqualTo(readingsToStore);
    }

    @Test
    @DisplayName("storeReadings should succeed when called multiple times")
    void storeReadingsShouldSucceedWhenCalledMultipleTimes() {
        var meterReadings = List.of(new ElectricityReading(FIXED_CLOCK, 1.0));
        var otherMeterReadings = List.of(new ElectricityReading(FIXED_CLOCK, 2.0));

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
    void storeReadingsShouldWriteSuppliedReadingsToCorrectMeter() {
        var meterReadings = List.of(new ElectricityReading(FIXED_CLOCK, 1.0));
        var otherMeterReadings = List.of(new ElectricityReading(FIXED_CLOCK, 2.0));

        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        meterReadingManager.storeReadings("00001", otherMeterReadings);

        assertThat(smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings())
                .isEqualTo(meterReadings);
    }

    @Test
    @DisplayName("readReadings should throw exception if supplied meterId is not persisted")
    void readReadingsShouldThrowExceptionIfSupplierNotPersisted() {
        assertThatThrownBy(() -> meterReadingManager.readReadings(SMART_METER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    @DisplayName("readReadings should return previously supplied readings for a known meterId")
    void readReadingsShouldReturnPreviouslySuppliedReadings() {
        // given
        var meterReadings = List.of(new ElectricityReading(FIXED_CLOCK, 1.0));
        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        // expect
        assertThat(meterReadingManager.readReadings(SMART_METER_ID)).isEqualTo(meterReadings);
    }
}
