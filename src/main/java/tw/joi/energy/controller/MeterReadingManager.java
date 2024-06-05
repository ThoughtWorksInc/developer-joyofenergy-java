package tw.joi.energy.controller;

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

        // check if given smartMeterId is valid
        if (smartMeterId == null || smartMeterId.isEmpty()) {
            throw new IllegalArgumentException("smartMeterId must be provided");
        }

        // check if given electricityReadings are valid
        if (electricityReadings == null || electricityReadings.isEmpty()) {
            throw new IllegalArgumentException("elecricity readings must be provided");
        }

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
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(smartMeterId);
        if (optionalSmartMeter.isEmpty()) {
            throw new IllegalArgumentException("smartMeterId does not exist");
        } else {
            SmartMeter smartMeter = optionalSmartMeter.get();
            return smartMeter.electricityReadings();
        }
    }
}
