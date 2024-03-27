package com.musala.drones.schedule;

import com.musala.drones.service.DroneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A schedule class to check battery level
 *
 * @author Rajinda
 * @version 1.0
 * @since 26/03/2024
 */
@Component
@EnableScheduling
@Slf4j
public class BatteryLevelCheckScheduler {

    private DroneService droneService;

    public BatteryLevelCheckScheduler(DroneService droneService) {
        this.droneService = droneService;
    }

    @Scheduled(cron = "${battery.level.check.schedule.con}")
    public void batteryLevelCheckTask() {
        try {
            log.info("BatteryLevelCheckScheduler called");
            droneService.checkDroneBatteryLevel();

        } catch (Exception exception) {
            log.error("BatteryLevelCheckScheduler exception : {}", exception.getMessage());
        }
    }
}
