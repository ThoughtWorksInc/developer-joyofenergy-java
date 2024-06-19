package tw.joi.energy.domain;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class SmartMeterTest {

    @Test
    void should_return_null_price_plan_id_when_no_price_plan_has_been_provided() {
        var smartMeter = new SmartMeter(null, Collections.emptyList());

        var pricePlanId = smartMeter.getPricePlanId();

        assertThat(pricePlanId).isNull();
    }
}
