package tw.joi.energy.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class SmartMeterTest {

    @Test
    void shouldReturnNullPricePlanIdWhenNoPricePlanHasBeenProvided() {
        var smartMeter = new SmartMeter(null, Collections.emptyList());

        var pricePlanId = smartMeter.getPricePlanId();

        assertThat(pricePlanId).isNull();
    }
}
