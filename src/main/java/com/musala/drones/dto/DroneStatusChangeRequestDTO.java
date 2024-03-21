package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musala.drones.util.AppConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Drone status change request
 *
 * @author Rajinda
 * @version 1.0
 * @since 20/03/2024
 */
@AllArgsConstructor
@Getter
@Setter
public class DroneStatusChangeRequestDTO implements Serializable {

    @JsonProperty("serialNumber")
    @Size(min = 1, max = 100, message = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED)
    private String serialNumber;

    @JsonProperty("status")
    @NotNull(message = AppConstants.DRONE_STATUS_MANDATORY)
    private String status;
}
