package tw.joi.energy;

import tw.joi.energy.config.TestData;
import tw.joi.energy.service.PricePlanComparator;

public class App {

    public static void main(String[] args) {
        var smartMeterRepository = TestData.smartMeterRepository();
        var pricePlanRepository = TestData.pricePlanService();
        var pricePlanComparator = new PricePlanComparator(pricePlanRepository, smartMeterRepository);

        var pricePlanComparisons = pricePlanComparator.calculatedCostForEachPricePlan("smart-meter-0");
        System.out.println("Price plan comparisons for smart-meter-0:");
        System.out.println(pricePlanComparisons);

        var cheapestPricePlans = pricePlanComparator.recommendCheapestPricePlans("smart-meter-0", 2);
        System.out.println("Cheapest price plans for smart-meter-0:");
        System.out.println(cheapestPricePlans);
    }
}
