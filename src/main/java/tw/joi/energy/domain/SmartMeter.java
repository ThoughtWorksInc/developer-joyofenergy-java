package tw.joi.energy.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.lang.Nullable;

public class SmartMeter {
    private final PricePlan pricePlan;
    private final List<ElectricityReading> electricityReadings;

    public SmartMeter(@Nullable PricePlan pricePlan, List<ElectricityReading> electricityReadings) {
        this.pricePlan = pricePlan;
        this.electricityReadings = new ArrayList<>(electricityReadings);
    }

    public List<ElectricityReading> electricityReadings() {
        return Collections.unmodifiableList(electricityReadings);
    }

    public void addReadings(List<ElectricityReading> electricityReadings) {
        this.electricityReadings.addAll(electricityReadings);
    }

    @Nullable public String getSmartMeterId() {
        if (null == pricePlan) {
            return null;
        }
        return pricePlan.getPlanName();
    }
}
