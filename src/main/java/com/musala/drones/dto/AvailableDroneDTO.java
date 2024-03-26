package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Available drone information
 *
 * @author Rajinda
 * @version 1.0
 * @since 25/03/2024
 */
@AllArgsConstructor
@Getter
@Setter
public class AvailableDroneDTO implements Serializable {

    @JsonProperty("serialNumber")
    private String serialNumber;

    @JsonProperty("model")
    private String model;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="%,d")
    @JsonProperty("weight")
    private Integer weight;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="%,d")
    @JsonProperty("capacity")
    private Integer capacity;
}
