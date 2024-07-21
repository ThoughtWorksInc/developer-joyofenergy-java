package tw.joi.energy.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tw.joi.energy.domain.SmartMeter;

class SmartMeterRepositoryTest {

    @Test
    @DisplayName("findById should return empty option when searching for non-existent id")
    void findById_shouldReturnEmptyOptionWhenSearchingForNonExistentId() {
        var repository = new SmartMeterRepository();

        assertThat(repository.findById("non-existent")).isEmpty();
    }

    @Test
    @DisplayName("findById should return appropriate smart meter if parameter exists in repository")
    void findById_shouldReturnSmartMeterIfParameterExistsInRepository() {
        var repository = new SmartMeterRepository();
        SmartMeter smartMeter0 = new SmartMeter(null, List.of());
        SmartMeter smartMeter1 = new SmartMeter(null, List.of());

        repository.save("smart-meter-0", smartMeter0);
        repository.save("smart-meter-1", smartMeter1);

        assertThat(repository.findById("smart-meter-0")).contains(smartMeter0);
        assertThat(repository.findById("smart-meter-1")).contains(smartMeter1);
    }
}
