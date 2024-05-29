package uk.tw.energy.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.repository.MeterReadingRepository;

public class MeterReadingRepositoryTest {

  private MeterReadingRepository meterReadingRepository;

  @BeforeEach
  public void setUp() {
    meterReadingRepository = new MeterReadingRepository(new HashMap<>());
  }

  @Test
  public void givenMeterIdThatDoesNotExistShouldReturnNull() {
    assertThat(meterReadingRepository.getReadings("unknown-id")).isEqualTo(Optional.empty());
  }

  @Test
  public void givenMeterReadingThatExistsShouldReturnMeterReadings() {
    meterReadingRepository.storeReadings("random-id", new ArrayList<>());
    assertThat(meterReadingRepository.getReadings("random-id"))
        .isEqualTo(Optional.of(new ArrayList<>()));
  }
}
