package tw.joi.energy.domain;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

/**
 * @param time point in time
 * @param readingInKwH energy consumed in total to this point in time in kWh
 */
public record ElectricityReading(Instant time, BigDecimal readingInKwH) {

    public ElectricityReading(Clock clock, double readingInKwH) {
        this(clock.instant(), BigDecimal.valueOf(readingInKwH));
    }
}
