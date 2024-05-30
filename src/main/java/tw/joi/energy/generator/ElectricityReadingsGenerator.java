package tw.joi.energy.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import tw.joi.energy.domain.ElectricityReading;

public class ElectricityReadingsGenerator {

  public List<ElectricityReading> generate(int number) {
    List<ElectricityReading> readings = new ArrayList<>();
    Instant now = Instant.now();
    BigDecimal previousReading  = BigDecimal.ONE;
    Instant previousReadingTime = now.minusSeconds(2 * number * 10L);

    Random readingRandomiser = new Random();

    for (int i = 0; i < number; i++) {
      double positiveIncrement = Math.abs(readingRandomiser.nextGaussian());
      BigDecimal currentReading = previousReading.add(
          BigDecimal.valueOf(positiveIncrement)).setScale(4, RoundingMode.CEILING);
      ElectricityReading electricityReading =
          new ElectricityReading(previousReadingTime.plusSeconds(i * 10L), currentReading);
      readings.add(electricityReading);
      previousReading = currentReading;
    }

    readings.sort(Comparator.comparing(ElectricityReading::time));
    return readings;
  }
}
