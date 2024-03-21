package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musala.drones.util.AppConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Medication information
 *
 * @author Rajinda
 * @version 1.0
 * @since 20/03/2024
 */
@AllArgsConstructor
@Getter
@Setter
public class MedicationRequestDTO implements Serializable {

    @JsonProperty("name")
    @Pattern(regexp = AppConstants.REGEXP_MEDICATION_NAME, message = AppConstants.INVALID_MEDICATION_NAME)
    @Size(min = 1, max = 250, message = AppConstants.MEDICATION_NAME_LENGTH_EXCEEDED)
    private String name;

    @JsonProperty("weight")
    @NotNull(message = AppConstants.EMPTY_MEDICATION_WEIGHT)
    private Integer weight;

    @JsonProperty("code")
    @Pattern(regexp = AppConstants.REGEXP_MEDICATION_CODE, message = AppConstants.INVALID_MEDICATION_CODE)
    @Size(min = 1, max = 100, message = AppConstants.EMPTY_MEDICATION_CODE)
    private String code;

    @JsonProperty("image")
    @NotEmpty(message = AppConstants.EMPTY_IMAGE)
    private byte[] image;

    @JsonProperty("serialNumber")
    @Size(min = 1, max = 100, message = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED)
    private String serialNumber;
}
