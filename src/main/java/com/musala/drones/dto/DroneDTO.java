package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Drone class to store drone information
 *
 * @author Rajinda
 * @version 1.0
 * @since 20/03/2024
 */
@Getter
@Setter
@AllArgsConstructor
public class DroneDTO implements Serializable {

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

    @JsonProperty("status")
    private String status;
}
