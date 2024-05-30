package uk.tw.energy.builders;

import java.util.ArrayList;
import java.util.List;

import uk.tw.energy.controller.StoreReadingsRequest;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

public class StoreReadingsRequestBuilder {

  private static final String DEFAULT_METER_ID = "id";

  private String smartMeterId = DEFAULT_METER_ID;
  private List<ElectricityReading> electricityReadings = new ArrayList<>();

  public StoreReadingsRequestBuilder setSmartMeterId(String smartMeterId) {
    this.smartMeterId = smartMeterId;
    return this;
  }

  public StoreReadingsRequestBuilder generateElectricityReadings() {
    return generateElectricityReadings(5);
  }

  public StoreReadingsRequestBuilder generateElectricityReadings(int number) {
    ElectricityReadingsGenerator readingsBuilder = new ElectricityReadingsGenerator();
    this.electricityReadings = readingsBuilder.generate(number);
    return this;
  }

  public StoreReadingsRequest build() {
    return new StoreReadingsRequest(smartMeterId, electricityReadings);
  }
}
