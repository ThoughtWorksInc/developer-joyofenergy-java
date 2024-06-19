package tw.joi.energy.repository;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class PricePlanRepositoryTest {

    @Test
    void should_return_empty_list_when_get_all_price_plans_given_no_price_plans_available() {
        var repository = new PricePlanRepository(emptyList());

        var allPlans = repository.getAllPricePlans();

        assertThat(allPlans).isEmpty();
    }
}
