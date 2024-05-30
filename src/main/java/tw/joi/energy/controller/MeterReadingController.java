package tw.joi.energy.controller;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.MeterReadingRepository;
import tw.joi.energy.repository.SmartMeterRepository;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    private final MeterReadingRepository meterReadingRepository;
    private final SmartMeterRepository smartMeterRepository;

    public MeterReadingController(
            MeterReadingRepository meterReadingRepository, SmartMeterRepository smartMeterRepository) {
        this.meterReadingRepository = meterReadingRepository;
        this.smartMeterRepository = smartMeterRepository;
    }

    @PostMapping()
    public ResponseEntity<?> storeReadings(@RequestBody StoreReadingsRequest request) {
        if (!request.isValid()) {
            return ResponseEntity.internalServerError().build();
        }
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(request.smartMeterId());
        if (optionalSmartMeter.isEmpty()) {
            smartMeterRepository.save(request.smartMeterId(), new SmartMeter(null, request.electricityReadings()));
        } else {
            SmartMeter smartMeter = optionalSmartMeter.get();
            smartMeter.addReadings(request.electricityReadings());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity readReadings(@PathVariable String smartMeterId) {
        Optional<SmartMeter> optionalSmartMeter = smartMeterRepository.findById(smartMeterId);
        if (optionalSmartMeter.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            SmartMeter smartMeter = optionalSmartMeter.get();
            return ResponseEntity.ok(smartMeter.electricityReadings());
        }
    }
}
