package tw.joi.energy.config;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.stream.Stream;
import tw.joi.energy.domain.ElectricityReading;

public class ElectricityReadingsGenerator {

    public static final double AVG_HOURLY_USAGE = 0.3;
    public static final double VARIANCE = 0.2;
    public static final double MIN_HOURLY_USAGE = AVG_HOURLY_USAGE - VARIANCE;
    public static final double MAX_HOURLY_USAGE = AVG_HOURLY_USAGE + VARIANCE;

    private ElectricityReadingsGenerator() {}

    public static Stream<ElectricityReading> generateElectricityReadingStream(int days) {
        return generateElectricityReadingStream(Clock.systemDefaultZone(), BigDecimal.ZERO, days);
    }

    // we'll provide hourly readings for the specified number of days assuming 24 hours a day
    // we'll assume that a house consumes ca 2700 kWh a year, so about 0.3 kWh per hour

    // the assumed starting point is the time on the clock, the ending point 24 hours later - so for 1 day, we'll get 25
    // readings
    public static Stream<ElectricityReading> generateElectricityReadingStream(
            Clock clock, BigDecimal initialReading, int days) {
        var now = clock.instant();
        var readingRandomiser = new Random();
        var seed = new ElectricityReading(now, initialReading);
        var lastTimeToBeSupplied = now.plus(days * 24L, ChronoUnit.HOURS);
        return Stream.iterate(
                seed, er -> !er.time().isAfter(lastTimeToBeSupplied), er -> {
                    var hoursWorthOfEnergy =
                            BigDecimal.valueOf(readingRandomiser.nextDouble(MIN_HOURLY_USAGE, MAX_HOURLY_USAGE));
                    return new ElectricityReading(
                            er.time().plus(1, ChronoUnit.HOURS),
                            er.readingInKwH().add(hoursWorthOfEnergy));
                });
    }
}
