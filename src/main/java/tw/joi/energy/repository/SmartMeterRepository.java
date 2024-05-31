package tw.joi.energy.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import tw.joi.energy.domain.SmartMeter;

public class SmartMeterRepository {
    private final Map<String, SmartMeter> smartMeters = new HashMap<>();

    public void save(String smartMeterId, SmartMeter smartMeter) {
        this.smartMeters.put(smartMeterId, smartMeter);
    }

    public Optional<SmartMeter> findById(String smartMeterId) {
        return Optional.ofNullable(this.smartMeters.get(smartMeterId));
    }
}
