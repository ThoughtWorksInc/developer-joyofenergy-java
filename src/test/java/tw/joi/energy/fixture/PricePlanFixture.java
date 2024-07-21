package tw.joi.energy.fixture;

import java.math.BigDecimal;
import tw.joi.energy.domain.PricePlan;

public class PricePlanFixture {

    public static final String WORST_PLAN_ID = "worst-supplier";
    public static final String BEST_PLAN_ID = "best-supplier";
    public static final String SECOND_BEST_PLAN_ID = "second-best-supplier";

    public static final PricePlan DEFAULT_PRICE_PLAN =
            new PricePlan(SECOND_BEST_PLAN_ID, "energy-supplier", BigDecimal.TWO);

    public static final PricePlan WORST_PRICE_PLAN = new PricePlan(WORST_PLAN_ID, null, BigDecimal.TEN);

    public static final PricePlan BEST_PRICE_PLAN = new PricePlan(BEST_PLAN_ID, null, BigDecimal.ONE);
}
