package tw.joi.energy.builders;

import java.util.ArrayList;
import java.util.List;
import tw.joi.energy.config.ElectricityReadingsGenerator;
import tw.joi.energy.controller.StoreReadingsRequest;
import tw.joi.energy.domain.ElectricityReading;

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
