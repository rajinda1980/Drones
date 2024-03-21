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
    public static final String BATTERY_CAPACITY_LOW = "The battery capacity must be greater than or equal to 1";
    public static final String BATTERY_CAPACITY_EXCEEDED = "The battery capacity must be less than or equal to 100";

    public static final String DRONE_REGISTERED = "The drone has been registered";
    public static final String DRONE_REGISTERED_EXCEPTION = "Drone is registered with this serial number";
    public static final String DRONE_DOES_NOT_EXIST = "The drone does not exist for the given serial number. Serial Number : ";

    public static final String INVALID_MEDICATION_NAME = "Invalid medication name. Only letters, numbers, hyphens, and underscores are allowed in the name";
    public static final String MEDICATION_NAME_LENGTH_EXCEEDED = "The medication name is mandatory and must not exceed 250 characters";
    public static final String INVALID_MEDICATION_CODE = "Invalid medication code. Only upper case letters, underscore and numbers are allowed in the code";
    public static final String EMPTY_MEDICATION_CODE = "The medication code is mandatory and must not exceed 100 characters";
    public static final String EMPTY_MEDICATION_WEIGHT = "The weight is mandatory";
    public static final String EMPTY_IMAGE = "The image name is mandatory";
    public static final String INVALID_IMAGE_TYPE = "Invalid image type. Supported types are PNG, JPG, or JPEG";
    public static final String UNKNOWN_IMAGE_TYPE = "Unknown image type. Supported types are PNG, JPG, or JPEG";

    public static final String DRONE_OVERWEIGHT = "Unable to load medication onto the drone due to overweight. The item weight should be less than or equal to ";
    public static final String DRONE_NOT_OCCUPIED = "Unable to load medication onto the drone as it is not occupied. Please select another drone";
    public static final String DRONE_STATUS_MANDATORY = "The drone status is mandatory";
    public static final String INVALID_DRONE_STATUS = "Invalid drone status. Valid statuses are : ";

    // Regular expressions
    public static final String REGEXP_DRONE_MODEL = "^(Lightweight|Middleweight|Cruiserweight|Heavyweight)$";
    public static final String REGEXP_MEDICATION_NAME = "[a-zA-Z0-9-_]*";
    public static final String REGEXP_MEDICATION_CODE = "[A-Z0-9_]*";

}
