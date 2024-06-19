package tw.joi.energy.repository;

import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.SmartMeter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SmartMeterRepositoryTest {

    @Test
    void should_return_empty_repository_when_given_anon_existent_id() {
        var repository = new SmartMeterRepository();

        assertThat(repository.findById("non-existent")).isEmpty();
    }

    @Test
    void should_save_and_find_smart_meters_given_avalid_smart_meter_and_id() {
        var repository = new SmartMeterRepository();
        SmartMeter smartMeter0 = new SmartMeter(null, List.of());
        SmartMeter smartMeter1 = new SmartMeter(null, List.of());

        repository.save("smart-meter-0", smartMeter0);
        repository.save("smart-meter-1", smartMeter1);

        assertThat(repository.findById("smart-meter-0")).contains(smartMeter0);
        assertThat(repository.findById("smart-meter-1")).contains(smartMeter1);
    }
}
