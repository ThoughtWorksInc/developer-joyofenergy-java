package tw.joi.energy.fixture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import tw.joi.energy.domain.ElectricityReading;

public class ElectricityReadingFixture {
    public static ElectricityReading createReading(LocalDateTime timeToRead, Double reading) {
        return new ElectricityReading(
                timeToRead.atZone(ZoneId.systemDefault()).toInstant(), BigDecimal.valueOf(reading));
    }

    public static ElectricityReading createReading(LocalDate dateToRead, Double reading) {
        return createReading(dateToRead.atStartOfDay(), reading);
    }
}
