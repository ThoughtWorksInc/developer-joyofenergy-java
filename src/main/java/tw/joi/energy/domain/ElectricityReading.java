package tw.joi.energy.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @param reading kWh
 */
public record ElectricityReading(Instant time, BigDecimal reading) {}
