package com.musala.drones.util;

import lombok.NoArgsConstructor;

/**
 * Store and manage application constant values
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@NoArgsConstructor
public final class AppConstants {

    public static final String CONTENT_TYPE = "application/json";
    public static final String SERIAL_NUMBER_LENGTH_EXCEED = "The serial number is mandatory and must not exceed 100 characters";
    public static final String INVALID_MODEL = "The model must be one of the following values: Lightweight, Middleweight, Cruiserweight, or Heavyweight";
    public static final String DRONE_WEIGHT_LOW = "The drone weight must be greater than or equal to 1 gram";
    public static final String DRONE_WEIGHT_EXCEEDED = "The drone weight must be less than or equal to 500 grams";
    public static final String BATTERY_CAPACITY_LOW = "The battery capacity must be greater than or equal to 100";
    public static final String BATTERY_CAPACITY_EXCEEDED = "The battery capacity must be less than or equal to 100";

    public static final String DRONE_REGISTERED = "The drone has been registered";
    public static final String DRONE_REGISTERED_EXCEPTION = "Drone is registered with this serial number";
}
