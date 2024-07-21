package tw.joi.energy.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SmartMeterTest {

    @Test
    @DisplayName("Price plan should be null if none has been supplied")
    void pricePlanShouldBeNullIfNoneHasBeenSupplied() {
        var smartMeter = new SmartMeter(null, emptyList());

        var pricePlanId = smartMeter.getPricePlanId();

        assertThat(pricePlanId).isNull();
    }
}
