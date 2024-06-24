package tw.joi.energy.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.SmartMeter;

class SmartMeterRepositoryTest {

    @Test
    void should_return_empty_smart_meter_when_find_by_id_given_a_non_existent_id() {
        var repository = new SmartMeterRepository();

        assertThat(repository.findById("non-existent")).isEmpty();
    }

    @Test
    void should_return_smart_meters_when_find_by_id_given_existent_smart_meter_ids() {
        var repository = new SmartMeterRepository();
        SmartMeter smartMeter0 = new SmartMeter(null, List.of());
        SmartMeter smartMeter1 = new SmartMeter(null, List.of());

        repository.save("smart-meter-0", smartMeter0);
        repository.save("smart-meter-1", smartMeter1);

        assertThat(repository.findById("smart-meter-0")).contains(smartMeter0);
        assertThat(repository.findById("smart-meter-1")).contains(smartMeter1);
    }
}
