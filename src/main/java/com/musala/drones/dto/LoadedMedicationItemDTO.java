package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Load medication item
 *
 * @author Rajinda
 * @version 1.0
 * @since 25/03/2024
 */
@AllArgsConstructor
@Getter
public class LoadedMedicationItemDTO implements Serializable {

    @JsonProperty("serialNumber")
    private String serialNumber;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="%,d")
    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("image")
    byte[] image;
}
