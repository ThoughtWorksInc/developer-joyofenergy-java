package tw.joi.energy.domain;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PricePlanTest {

    @Test
    public void get_energy_supplier_should_return_the_energy_supplier_given_supplier_is_existent() {
        PricePlan pricePlan = new PricePlan(null, "Energy Supplier Name", null, null);

        assertThat(pricePlan.getEnergySupplier()).isEqualTo("Energy Supplier Name");
    }

    @Test
    public void get_price_should_return_the_base_price_given_an_ordinary_date_time() throws Exception {
        LocalDateTime normalDateTime = LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0);
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, emptyList());

        BigDecimal price = pricePlan.getPrice(normalDateTime);

        assertThat(price).isCloseTo(BigDecimal.ONE, Percentage.withPercentage(1));
    }

    @Test
    public void get_unit_rate_should_return_unit_rate_given_unit_rate_is_present() {
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.TWO, null);
        pricePlan.setPlanName("test-price-plan");
        pricePlan.setEnergySupplier("test-energy-supplier");

        BigDecimal rate = pricePlan.getUnitRate();

        assertThat(rate).isEqualTo(BigDecimal.TWO);
    }
}
