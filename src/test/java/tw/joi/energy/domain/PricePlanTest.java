package tw.joi.energy.domain;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PricePlanTest {

    @Test
    @DisplayName("Get energy supplier should return supplier if not null")
    public void getSupplierShouldReturnSupplierIfNotNull() {
        PricePlan pricePlan = new PricePlan("Test Plan Name", "Energy Supplier Name", BigDecimal.ONE);

        assertThat(pricePlan.getEnergySupplier()).isEqualTo("Energy Supplier Name");
    }

    @Test
    @DisplayName("Get price should return price given non-peak date and time")
    public void getPriceShouldReturnPriceGivenNonPeakDateAndTime() {
        ZonedDateTime nonPeakDateTime =
                ZonedDateTime.of(LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0), ZoneId.of("GMT"));
        // the price plan has no peak days, so all times are non-peak
        PricePlan pricePlan = new PricePlan("test plan", "test supplier", BigDecimal.ONE);

        BigDecimal price = pricePlan.getPrice(nonPeakDateTime);

        assertThat(price).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Get unit rate should return unit rate if not null")
    public void getUnitRateShouldReturnUnitRateIfNotNull() {
        PricePlan pricePlan = new PricePlan("test-price-plan", "test-energy-supplier", BigDecimal.TWO);

        BigDecimal rate = pricePlan.getUnitRate();

        assertThat(rate).isEqualByComparingTo(BigDecimal.TWO);
    }
}
