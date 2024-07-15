package tw.joi.energy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;

class SmartMeterTest {

    @Test
    @DisplayName("Price plan should be null if none has been supplied")
    void price_plan_id_should_be_null_given_no_price_plan_has_been_provided() {
        var smartMeter = new SmartMeter(null, emptyList());

        var pricePlanId = smartMeter.getPricePlanId();

        assertThat(pricePlanId).isNull();
    }
}
