package com.musala.drones.dto;

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
    private String serialNumber;
    private String model;
    private Integer weight;
    private Integer capacity;
    private String status;
}
