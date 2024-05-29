package uk.tw.energy.controller;

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
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.repository.MeterReadingRepository;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

  private final MeterReadingRepository meterReadingRepository;

  public MeterReadingController(MeterReadingRepository meterReadingRepository) {
    this.meterReadingRepository = meterReadingRepository;
  }

  @PostMapping("/store")
  public ResponseEntity storeReadings(@RequestBody MeterReadings meterReadings) {
    if (!isMeterReadingsValid(meterReadings)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    meterReadingRepository.storeReadings(
        meterReadings.smartMeterId(), meterReadings.electricityReadings());
    return ResponseEntity.ok().build();
  }

  private boolean isMeterReadingsValid(MeterReadings meterReadings) {
    String smartMeterId = meterReadings.smartMeterId();
    List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
    return smartMeterId != null
        && !smartMeterId.isEmpty()
        && electricityReadings != null
        && !electricityReadings.isEmpty();
  }

  @GetMapping("/read/{smartMeterId}")
  public ResponseEntity readReadings(@PathVariable String smartMeterId) {
    Optional<List<ElectricityReading>> readings = meterReadingRepository.getReadings(smartMeterId);
    return readings.isPresent()
        ? ResponseEntity.ok(readings.get())
        : ResponseEntity.notFound().build();
  }
}
