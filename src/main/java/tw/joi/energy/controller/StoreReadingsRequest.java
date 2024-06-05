package tw.joi.energy.controller;

import java.util.List;
import tw.joi.energy.domain.ElectricityReading;

public record StoreReadingsRequest(String smartMeterId, List<ElectricityReading> electricityReadings) {}
