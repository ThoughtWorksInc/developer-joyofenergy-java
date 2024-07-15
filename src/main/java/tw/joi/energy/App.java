package tw.joi.energy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import tw.joi.energy.config.ElectricityReadingsGenerator;
import tw.joi.energy.config.TestData;
import tw.joi.energy.domain.SmartMeter;
import tw.joi.energy.repository.PricePlanRepository;
import tw.joi.energy.repository.SmartMeterRepository;
import tw.joi.energy.service.MeterReadingManager;
import tw.joi.energy.service.PricePlanComparator;

public class App {

    public static final String TEST_SMART_METER = "smart-meter-0";
    public static final int RESULTS_COUNT_LIMIT = 2;

    public static void main(String[] args) {
        var smartMeterRepository = TestData.smartMeterRepository();
        var pricePlanRepository = TestData.pricePlanService();
        var meterReadingManager = new MeterReadingManager(smartMeterRepository);
        var pricePlanComparator = new PricePlanComparator(pricePlanRepository, smartMeterRepository);

        printAllAvailablePricePlans(pricePlanRepository);

        printSmartMeterInformation(smartMeterRepository, "Before storing readings...");
        var readingsToSave = ElectricityReadingsGenerator.generateElectricityReadingStream(3).toList();
        meterReadingManager.storeReadings(TEST_SMART_METER, readingsToSave);
        printSmartMeterInformation(smartMeterRepository, "After storing readings...");

        var storedReadings = meterReadingManager.readReadings(TEST_SMART_METER);
        System.out.println("\nReadings retrieved from '" + TEST_SMART_METER + "':\n\t" + storedReadings);

        var cheapestPricePlans = pricePlanComparator.recommendCheapestPricePlans(TEST_SMART_METER, RESULTS_COUNT_LIMIT);
        printCheapestPlans(cheapestPricePlans);
    }

    private static void printCheapestPlans(List<Map.Entry<String, BigDecimal>> cheapestPricePlans) {
        System.out.println("\nTop " + RESULTS_COUNT_LIMIT + " cheapest price plans for '" + TEST_SMART_METER + "':");
        System.out.println("\t" + cheapestPricePlans);
    }

    private static void printSmartMeterInformation(SmartMeterRepository smartMeterRepository, String description) {
        SmartMeter smartMeter =
                smartMeterRepository.findById(App.TEST_SMART_METER).get();
        System.out.println("[" + description + "] Meter information for '" + App.TEST_SMART_METER + "':");
        System.out.println("\t" + smartMeter);
    }

    private static void printAllAvailablePricePlans(PricePlanRepository pricePlanRepository) {
        System.out.println("\nAvailable price plans:");
        pricePlanRepository.getAllPricePlans().forEach(p -> System.out.println("\t" + p));
        System.out.println();
    }
}
