package uk.tw.energy.domain;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

public class PricePlanTest {

  private final String ENERGY_SUPPLIER_NAME = "Energy Supplier Name";

  @Test
  public void shouldReturnTheEnergySupplierGivenInTheConstructor() {
    PricePlan pricePlan = new PricePlan(null, ENERGY_SUPPLIER_NAME, null);

    assertThat(pricePlan.getEnergySupplier()).isEqualTo(ENERGY_SUPPLIER_NAME);
  }

  @Test
  public void shouldReturnTheBasePriceGivenAnOrdinaryDateTime() throws Exception {
    LocalDateTime normalDateTime = LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0);
    PricePlan pricePlan =
        new PricePlan(null, null, BigDecimal.ONE);

    BigDecimal price = pricePlan.getPrice(normalDateTime);

    assertThat(price).isCloseTo(BigDecimal.ONE, Percentage.withPercentage(1));
  }
}
