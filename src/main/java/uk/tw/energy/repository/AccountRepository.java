package uk.tw.energy.repository;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AccountRepository {

  private final Map<String, String> smartMeterToPricePlanAccounts;

  public AccountRepository(Map<String, String> smartMeterToPricePlanAccounts) {
    this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
  }

  public String getPricePlanIdForSmartMeterId(String smartMeterId) {
    return smartMeterToPricePlanAccounts.get(smartMeterId);
  }
}
