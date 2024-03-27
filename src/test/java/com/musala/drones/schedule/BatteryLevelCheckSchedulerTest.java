package com.musala.drones.schedule;

import com.musala.drones.service.DroneService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.BDDMockito.*;

/**
 * Scheduler test cases for schedulers
 *
 * @author Rajinda
 * @version 1.0
 * @since 27/03/2024
 */
@SpringBootTest
@EnableScheduling
@TestPropertySource(
        properties = {"spring.main.allow-bean-definition-overriding=true"}
)
public class BatteryLevelCheckSchedulerTest {

    @InjectMocks
    BatteryLevelCheckScheduler scheduler;

    @Mock
    DroneService droneService;

    @Test
    void testBatteryLevelCheckTask() throws Exception {
        doNothing().when(droneService).checkDroneBatteryLevel();
        scheduler.batteryLevelCheckTask();
        verify(droneService, times(1)).checkDroneBatteryLevel();
    }
}
