package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musala.drones.util.AppConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Drone information
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@Getter
@Setter
public class DroneRequestDTO implements Serializable {

    @JsonProperty("serialNumber")
    @Size(min = 1, max = 5, message = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED)
    private String serialNumber;

    @JsonProperty("model")
    @Pattern(regexp = "^(Lightweight|Middleweight|Cruiserweight|Heavyweight)$", message = AppConstants.INVALID_MODEL)
    private String model;

    @JsonProperty("weight")
    @Min(value = 1, message =  AppConstants.DRONE_WEIGHT_LOW)
    @Max(value = 500, message = AppConstants.DRONE_WEIGHT_EXCEEDED)
    private Integer weight;

    @JsonProperty("capacity")
    @Min(value = 1, message = AppConstants.BATTERY_CAPACITY_LOW)
    @Max(value = 100, message = AppConstants.BATTERY_CAPACITY_EXCEEDED)
    private Integer capacity;
}
