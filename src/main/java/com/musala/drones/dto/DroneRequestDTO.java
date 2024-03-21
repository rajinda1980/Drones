package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.musala.drones.util.AppConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Getter
@Setter
public class DroneRequestDTO implements Serializable {

    @JsonProperty("serialNumber")
    @Size(min = 1, max = 100, message = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED)
    private String serialNumber;

    @JsonProperty("model")
    @Pattern(regexp = AppConstants.REGEXP_DRONE_MODEL, message = AppConstants.INVALID_MODEL)
    private String model;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="%,d")
    @JsonProperty("weight")
    @Min(value = 1, message =  AppConstants.DRONE_WEIGHT_LOW)
    @Max(value = 500, message = AppConstants.DRONE_WEIGHT_EXCEEDED)
    private Integer weight;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="%,d")
    @JsonProperty("capacity")
    @Min(value = 1, message = AppConstants.BATTERY_CAPACITY_LOW)
    @Max(value = 100, message = AppConstants.BATTERY_CAPACITY_EXCEEDED)
    private Integer capacity;
}
