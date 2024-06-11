package tw.joi.energy.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

public class PricePlanTest {

    @Test
    public void shouldReturnTheEnergySupplierGivenInTheConstructor() {
        PricePlan pricePlan = new PricePlan(null, "Energy Supplier Name", null, null);

        assertThat(pricePlan.getEnergySupplier()).isEqualTo("Energy Supplier Name");
    }

    @Test
    public void shouldReturnTheBasePriceGivenAnOrdinaryDateTime() throws Exception {
        LocalDateTime normalDateTime = LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0);
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.ONE, emptyList());

        BigDecimal price = pricePlan.getPrice(normalDateTime);

        assertThat(price).isCloseTo(BigDecimal.ONE, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReturnUnitPrice() {
        PricePlan pricePlan = new PricePlan(null, null, BigDecimal.TWO, null);
        pricePlan.setPlanName("test-price-plan");
        pricePlan.setEnergySupplier("test-energy-supplier");

        BigDecimal rate = pricePlan.getUnitRate();

        assertThat(rate).isEqualTo(BigDecimal.TWO);
    }
}
