package tw.joi.energy.domain;

import static java.util.Collections.emptySet;
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
    public void get_energy_supplier_should_return_the_energy_supplier_given_supplier_is_existent() {
        PricePlan pricePlan = new PricePlan("Test Plan Name", "Energy Supplier Name", BigDecimal.ONE, emptySet());

        assertThat(pricePlan.getEnergySupplier()).isEqualTo("Energy Supplier Name");
    }

    @Test
    @DisplayName("Get price should return price given non-peak date and time")
    public void get_price_should_return_the_base_price_given_an_ordinary_date_time() {
        ZonedDateTime nonPeakDateTime = ZonedDateTime.of(LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0),
            ZoneId.of("GMT"));
        // the price plan has no peak days....
        PricePlan pricePlan = new PricePlan("test plan", "test supplier", BigDecimal.ONE, emptySet());

        BigDecimal price = pricePlan.getPrice(nonPeakDateTime);

        assertThat(price).isEqualTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Get unit rate should return unit rate if no null")
    public void get_unit_rate_should_return_unit_rate_given_unit_rate_is_present() {
        PricePlan pricePlan = new PricePlan("test-price-plan", "test-energy-supplier", BigDecimal.TWO, emptySet());

        BigDecimal rate = pricePlan.getUnitRate();

        assertThat(rate).isEqualTo(BigDecimal.TWO);
    }
}
