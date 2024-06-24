package tw.joi.energy.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class SmartMeterTest {

    @Test
    void should_return_null_when_get_price_plan_id_given_no_price_plan_has_been_provided() {
        var smartMeter = new SmartMeter(null, Collections.emptyList());

        var pricePlanId = smartMeter.getPricePlanId();

        assertThat(pricePlanId).isNull();
    }
}
