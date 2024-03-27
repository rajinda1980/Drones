package com.musala.drones.schedule;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Cron expression test cases for schedulers
 *
 * @author Rajinda
 * @version 1.0
 * @since 27/03/2024
 */
@SpringBootTest(properties = "0 */2 * * * ?")
public class CronExpressionTester {

    @SpyBean
    BatteryLevelCheckScheduler batteryLevelCheckScheduler;

    @Test
    void testBatteryLevelCheckCron() throws Exception {
        Awaitility.await()
                .atMost(Duration.of(10000, ChronoUnit.MILLIS))
                .untilAsserted(() -> Mockito.verify(batteryLevelCheckScheduler, Mockito.atLeast(1)).batteryLevelCheckTask());
    }
}
