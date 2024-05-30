package tw.joi.energy.domain;

import java.util.List;

public record MeterReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {}
