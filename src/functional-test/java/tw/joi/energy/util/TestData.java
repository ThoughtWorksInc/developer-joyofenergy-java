package tw.joi.energy.util;

import java.math.BigDecimal;
import java.util.List;
import tw.joi.energy.config.ElectricityReadingsGenerator;
import tw.joi.energy.domain.PricePlan;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.SmartMeterRepository;
import tw.joi.energy.repository.PricePlanRepository;

public final class TestData {

    private static final PricePlan MOST_EVIL_PRICE_PLAN =
            new PricePlan("price-plan-0", "Dr Evil's Dark Energy", BigDecimal.TEN);
    private static final PricePlan RENEWABLES_PRICE_PLAN =
            new PricePlan("price-plan-1", "The Green Eco", BigDecimal.valueOf(2));
    private static final PricePlan STANDARD_PRICE_PLAN =
            new PricePlan("price-plan-2", "Power for Everyone", BigDecimal.ONE);

    public static SmartMeterRepository smartMeterRepository() {
        var generator = new ElectricityReadingsGenerator();
        var smartMeterRepository = new SmartMeterRepository();
        smartMeterRepository.save("smart-meter-0", new SmartMeter(MOST_EVIL_PRICE_PLAN, generator.generate(20)));
        smartMeterRepository.save("smart-meter-1", new SmartMeter(RENEWABLES_PRICE_PLAN, generator.generate(20)));
        smartMeterRepository.save("smart-meter-2", new SmartMeter(MOST_EVIL_PRICE_PLAN, generator.generate(20)));
        smartMeterRepository.save("smart-meter-3", new SmartMeter(STANDARD_PRICE_PLAN, generator.generate(20)));
        smartMeterRepository.save("smart-meter-4", new SmartMeter(RENEWABLES_PRICE_PLAN, generator.generate(20)));
        return smartMeterRepository;
    }

    public static PricePlanRepository pricePlanService() {
        return new PricePlanRepository(List.of(MOST_EVIL_PRICE_PLAN, RENEWABLES_PRICE_PLAN, STANDARD_PRICE_PLAN));
    }
}
