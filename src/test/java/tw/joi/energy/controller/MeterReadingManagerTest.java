package tw.joi.energy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import tw.joi.energy.config.ElectricityReadingsGenerator;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.repository.SmartMeterRepository;
import tw.joi.energy.service.MeterReadingManager;

public class MeterReadingManagerTest {

    private static final String SMART_METER_ID = "10101010";
    private final SmartMeterRepository smartMeterRepository = new SmartMeterRepository();
    private final MeterReadingManager meterReadingManager = new MeterReadingManager(smartMeterRepository);

    @Test
    public void givenNoMeterIdIsSuppliedWhenStoringShouldThrowException() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(null, Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }

    @Test
    public void givenEmptyMeterReadingShouldReturnErrorResponse() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    public void givenNullReadingsAreSuppliedWhenStoringShouldReturnErrorResponse() {
        assertThatThrownBy(() -> meterReadingManager.storeReadings(SMART_METER_ID, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("readings");
    }

    @Test
    public void givenMultipleBatchesOfMeterReadingsShouldStore() {
        var meterReadings = new ElectricityReadingsGenerator().generate(5);
        var otherMeterReadings = new ElectricityReadingsGenerator().generate(5);

        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        meterReadingManager.storeReadings(SMART_METER_ID, otherMeterReadings);

        List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
        expectedElectricityReadings.addAll(meterReadings);
        expectedElectricityReadings.addAll(otherMeterReadings);

        assertThat(smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings())
                .isEqualTo(expectedElectricityReadings);
    }

    @Test
    public void givenMeterReadingsAssociatedWithTheUserShouldStoreAssociatedWithUser() {
        var meterReadings = new ElectricityReadingsGenerator().generate(5);
        var otherMeterReadings = new ElectricityReadingsGenerator().generate(5);

        meterReadingManager.storeReadings(SMART_METER_ID, meterReadings);
        meterReadingManager.storeReadings("00001", otherMeterReadings);

        assertThat(smartMeterRepository.findById(SMART_METER_ID).get().electricityReadings())
                .isEqualTo(meterReadings);
    }

    @Test
    public void givenMeterIdThatIsNotRecognisedShouldReturnNotFound() {
        assertThatThrownBy(() -> meterReadingManager.readReadings(SMART_METER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("smartMeterId");
    }
}
