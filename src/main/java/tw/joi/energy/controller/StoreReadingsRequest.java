package tw.joi.energy.controller;

import java.util.List;

import tw.joi.energy.domain.ElectricityReading;

public record StoreReadingsRequest(
    String smartMeterId, List<ElectricityReading> electricityReadings) {

  boolean isValid() {
    String smartMeterId = smartMeterId();
    List<ElectricityReading> electricityReadings = electricityReadings();
    return smartMeterId != null
        && !smartMeterId.isEmpty()
        && electricityReadings != null
        && !electricityReadings.isEmpty();
  }
}
