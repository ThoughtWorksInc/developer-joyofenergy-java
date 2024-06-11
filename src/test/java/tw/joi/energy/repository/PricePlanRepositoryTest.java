package tw.joi.energy.repository;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PricePlanRepositoryTest {

    @Test
    void shouldReturnEmptyListWhenNoPricePlansAvailable() {
        var repository = new PricePlanRepository(emptyList());

        var allPlans = repository.getAllPricePlans();

        assertThat(allPlans).isEmpty();
    }
}
