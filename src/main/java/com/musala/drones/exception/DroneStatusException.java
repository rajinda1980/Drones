package com.musala.drones.exception;

/**
 * Exceptions related to drone statuses
 *
 * @author Rajinda
 * @version 1.0
 * @since 21/03/2024
 */
public class DroneStatusException extends RuntimeException {
    public DroneStatusException(String msg) {
        super(msg);
    }
}
