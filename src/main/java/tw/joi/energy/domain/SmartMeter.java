package tw.joi.energy.domain;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmartMeter {
    private final PricePlan pricePlan;
    private final List<ElectricityReading> electricityReadings;

    public SmartMeter(PricePlan pricePlan, List<ElectricityReading> electricityReadings) {
        this.pricePlan = pricePlan;
        this.electricityReadings = new ArrayList<>(requireNonNull(electricityReadings));
    }

    public List<ElectricityReading> electricityReadings() {
        return Collections.unmodifiableList(electricityReadings);
    }

    public void addReadings(List<ElectricityReading> electricityReadings) {
        this.electricityReadings.addAll(electricityReadings);
    }

    public String getPricePlanId() {
        if (null == pricePlan) {
            return null;
        }
        return pricePlan.getPlanName();
    }
}
