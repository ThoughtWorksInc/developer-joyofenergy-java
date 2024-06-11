package tw.joi.energy.service;

import java.util.List;
import java.util.Optional;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.SmartMeterRepository;

public class MeterReadingManager {
    private final SmartMeterRepository smartMeterRepository;

    public MeterReadingManager(SmartMeterRepository smartMeterRepository) {
        this.smartMeterRepository = smartMeterRepository;
    }

    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {

        // is smartMeterId empty?
        if (smartMeterId == null || smartMeterId.isEmpty()) {
            throw new IllegalArgumentException("smartMeterId must be provided");
        }

        // is valid?
        validateElectricityReadings(electricityReadings);

        // save
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(smartMeterId);
        if (optionalSmartMeter.isEmpty()) {
            smartMeterRepository.save(smartMeterId, new SmartMeter(null, electricityReadings));
        } else {
            SmartMeter smartMeter = optionalSmartMeter.get();
            smartMeter.addReadings(electricityReadings);
        }
    }

    public List<ElectricityReading> readReadings(String smartMeterId) {
        Optional<SmartMeter> sm = smartMeterRepository.findById(smartMeterId);
        if (sm.isEmpty()) {
            throw new IllegalArgumentException("smartMeterId does not exist");
        } else {
            SmartMeter smartMeter = sm.get();
            var collection = smartMeter.electricityReadings();
            return collection;
        }
    }

    private static void validateElectricityReadings(List<ElectricityReading> electricityReadings) {
        if (electricityReadings == null || electricityReadings.isEmpty()) {
            throw new IllegalArgumentException("electricity readings must be provided");
        }
    }
}
