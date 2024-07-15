package tw.joi.energy.repository;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PricePlanRepositoryTest {

    @Test
    @DisplayName("Should return empty list of plans if none available")
    void should_return_empty_list_when_get_all_price_plans_given_no_price_plans_available() {
        var repository = new PricePlanRepository(emptyList());

        var allPlans = repository.getAllPricePlans();

        assertThat(allPlans).isEmpty();
    }
}
