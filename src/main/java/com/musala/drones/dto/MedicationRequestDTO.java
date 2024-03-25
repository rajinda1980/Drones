package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Getter
@Setter
public class MedicationRequestDTO extends MRequestWithoutImageDTO implements Serializable {

    @JsonProperty("image")
    @NotEmpty(message = AppConstants.EMPTY_IMAGE)
    private byte[] image;

    public MedicationRequestDTO(String name, Integer weight, String code, byte[] image, String serialNumber) {
        super(name, weight, code, serialNumber);
        this.image = image;
    }
}
