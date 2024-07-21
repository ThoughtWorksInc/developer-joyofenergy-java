package tw.joi.energy.domain;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    //    public void removeReadings(List<ElectricityReading> itemsToRemove) {
    //        this.electricityReadings.removeAll(itemsToRemove);
    //    }

    public String getPricePlanId() {
        if (null == pricePlan) {
            return null;
        }
        return pricePlan.getPlanName();
    }

    @Override
    public String toString() {
        return "PricePlan=" + pricePlan + ", \n\tElectricityReadings:\n\t\t"
                + electricityReadings.stream().map(ElectricityReading::toString).collect(Collectors.joining("\n\t\t"));
    }
}
