package tw.joi.energy.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tw.joi.energy.repository.AccountRepository;

public class AccountServiceTest {

  private static final String PRICE_PLAN_ID = "price-plan-id";
  private static final String SMART_METER_ID = "smart-meter-id";

  private AccountRepository accountRepository;

  @BeforeEach
  public void setUp() {
    Map<String, String> smartMeterToPricePlanAccounts = new HashMap<>();
    smartMeterToPricePlanAccounts.put(SMART_METER_ID, PRICE_PLAN_ID);

    accountRepository = new AccountRepository(smartMeterToPricePlanAccounts);
  }

  @Test
  public void givenTheSmartMeterIdReturnsThePricePlanId() throws Exception {
    assertThat(accountRepository.getPricePlanIdForSmartMeterId(SMART_METER_ID))
        .isEqualTo(PRICE_PLAN_ID);
  }
}
