package tw.joi.energy.fixture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import tw.joi.energy.domain.ElectricityReading;

public class ElectricityReadingFixture {
    public static ElectricityReading createReading(ZonedDateTime timeToRead, Double reading) {
        return new ElectricityReading(
                timeToRead.toInstant(), BigDecimal.valueOf(reading));
    }

    public static ElectricityReading createReading(LocalDate dateToRead, ZoneId zoneId, Double reading) {
        return createReading(ZonedDateTime.of(dateToRead.atStartOfDay(), zoneId), reading);
    }
}
