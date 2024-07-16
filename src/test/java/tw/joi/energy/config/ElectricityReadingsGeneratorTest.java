package tw.joi.energy.config;

import static org.assertj.core.api.Assertions.assertThat;
import static tw.joi.energy.config.ElectricityReadingsGenerator.MAX_HOURLY_USAGE;
import static tw.joi.energy.config.ElectricityReadingsGenerator.MIN_HOURLY_USAGE;
import static tw.joi.energy.config.ElectricityReadingsGenerator.generateElectricityReadingStream;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.ElectricityReading;

class ElectricityReadingsGeneratorTest {

    @Test
    @DisplayName("Stream for one day should have 25 entries")
    void streamShouldHave25EntriesForOneDay() {
        assertThat(generateElectricityReadingStream(1).count()).isEqualTo(25);
    }

    @Test
    @DisplayName("Stream for two days should have 49 entries")
    void streamShouldHave49EntriesForTwoDays() {
        assertThat(generateElectricityReadingStream(2).count()).isEqualTo(49);
    }

    @Test
    @DisplayName("Stream for one day should end 24 hours after initial entry")
    void streamShouldHave24HoursAfterInitialEntry() {
        var streamAsList = generateElectricityReadingStream(1).toList();
        var firstEntry = streamAsList.getFirst();
        var lastEntry = streamAsList.getLast();
        assertThat(Duration.between(firstEntry.time(), lastEntry.time())).hasHours(24);
    }

    @Test
    @DisplayName("Stream entries should be one hour apart")
    void streamEntriesShouldBeOneHourApart() {
        validateOrderedPairsOfEntries(generateElectricityReadingStream(1), (earlierReading, laterReading) -> assertThat(
                        Duration.between(earlierReading.time(), laterReading.time()))
                .hasHours(1));
    }

    @Test
    @DisplayName("Stream entries should have an increasing energy consumption over time")
    void streamEntriesShouldHaveAnIncreasingEnergyConsumptionOverTime() {
        validateOrderedPairsOfEntries(generateElectricityReadingStream(1), (earlierReading, laterReading) -> assertThat(
                        laterReading.readingInKwH().compareTo(earlierReading.readingInKwH()))
                .isEqualTo(1));
    }

    @Test
    @DisplayName("Stream entries should have an energy consumption in the expected reange")
    void streamEntriesShouldHaveAnIncreasingEnergyConsumption() {
        var min = BigDecimal.valueOf(MIN_HOURLY_USAGE);
        var max = BigDecimal.valueOf(MAX_HOURLY_USAGE);

        validateOrderedPairsOfEntries(generateElectricityReadingStream(1), (earlierReading, laterReading) -> {
            var energyBetweenReadings = laterReading.readingInKwH().subtract(earlierReading.readingInKwH());
            assertThat(energyBetweenReadings.compareTo(min)).isEqualTo(1);
            assertThat(energyBetweenReadings.compareTo(max)).isEqualTo(-1);
        });
    }

    private void validateOrderedPairsOfEntries(
            Stream<ElectricityReading> stream, BiConsumer<ElectricityReading, ElectricityReading> validator) {
        var streamAsList = stream.toList();
        for (int i = 1; i <= streamAsList.size() - 1; i++) {
            var laterElectricityReading = streamAsList.get(i);
            var earlierElectricityReading = streamAsList.get(i - 1);
            validator.accept(earlierElectricityReading, laterElectricityReading);
        }
    }
}
