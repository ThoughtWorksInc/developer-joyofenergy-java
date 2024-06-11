package tw.joi.energy.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import tw.joi.energy.domain.SmartMeter;

public class SmartMeterRepository {
    private final Map<String, SmartMeter> smartMeters = new HashMap<>();

    // save new smart meter info
    public void save(String id, SmartMeter sm) {
        this.smartMeters.put(id, sm);
    }

    // find meter by id
    public Optional<SmartMeter> findById(String id) {
        return Optional.ofNullable(this.smartMeters.get(id));
    }

    // get all the meters
    public Iterable<SmartMeter> getAll() {
        return smartMeters.values();
    }
}
