package tw.joi.energy.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.joi.energy.domain.ElectricityReading;
import tw.joi.energy.repository.MeterReadingRepository;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

  private final MeterReadingRepository meterReadingRepository;

  public MeterReadingController(MeterReadingRepository meterReadingRepository) {
    this.meterReadingRepository = meterReadingRepository;
  }

  @PostMapping()
  public ResponseEntity<?> storeReadings(@RequestBody StoreReadingsRequest meterReadings) {
    if (!meterReadings.isValid()) {
      return ResponseEntity.internalServerError().build();
    }
    meterReadingRepository.storeReadings(
        meterReadings.smartMeterId(), meterReadings.electricityReadings());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/read/{smartMeterId}")
  public ResponseEntity readReadings(@PathVariable String smartMeterId) {
    Optional<List<ElectricityReading>> readings = meterReadingRepository.getReadings(smartMeterId);
    return readings.isPresent()
        ? ResponseEntity.ok(readings.get())
        : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }
}
