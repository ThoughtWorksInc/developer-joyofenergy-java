package tw.joi.energy.config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import tw.joi.energy.domain.ElectricityReading;

public class ElectricityReadingsGenerator {

    private ElectricityReadingsGenerator() {}

    public static Stream<ElectricityReading> generateElectricityReadingStream(int days) {
        return generateElectricityReadingStream(Clock.systemDefaultZone(), BigDecimal.ZERO, days);
    }

    // we'll provide hourly readings for the specified number of days assuming 24 hours a day
    // we'll assume that a house consumes ca 2700 kWh a year, so about 0.3 kWh per hour
    public static Stream<ElectricityReading> generateElectricityReadingStream(Clock clock, BigDecimal initialReading, int days) {
        var now = clock.instant();
        var readingRandomiser = new Random();
        var seed = new ElectricityReading(now, initialReading);
        var lastTimeToBeSupplied = now.plus(days * 24, ChronoUnit.HOURS);
        return Stream.iterate(seed, er -> er.time().equals(lastTimeToBeSupplied) || er.time().isAfter(lastTimeToBeSupplied),
            er -> {
            var hoursWorthOfEnergy = BigDecimal.valueOf(readingRandomiser.nextDouble(0.3 - 0.2, 0.3 + 0.2));
            return new ElectricityReading(er.time().plus(1, ChronoUnit.HOURS), er.readingInKwH().add(hoursWorthOfEnergy));
            });
    }
}
