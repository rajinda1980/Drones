package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * Medication information
 *
 * @author Rajinda
 * @version 1.0
 * @since 22/03/2024
 */
@AllArgsConstructor
@Getter
@Setter
public class MRequestWithoutImageDTO implements Serializable {

    @JsonProperty("name")
    @Pattern(regexp = AppConstants.REGEXP_MEDICATION_NAME, message = AppConstants.INVALID_MEDICATION_NAME)
    @Size(min = 1, max = 250, message = AppConstants.MEDICATION_NAME_LENGTH_EXCEEDED)
    private String name;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="%,d")
    @JsonProperty("weight")
    @NotNull(message = AppConstants.EMPTY_MEDICATION_WEIGHT)
    private Integer weight;

    @JsonProperty("code")
    @Pattern(regexp = AppConstants.REGEXP_MEDICATION_CODE, message = AppConstants.INVALID_MEDICATION_CODE_TEXT)
    @Size(min = 1, max = 100, message = AppConstants.INVALID_MEDICATION_CODE_LENGTH)
    private String code;

    @JsonProperty("serialNumber")
    @Size(min = 1, max = 100, message = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED)
    private String serialNumber;
}
