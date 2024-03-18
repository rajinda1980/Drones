package com.musala.drones.exception;

/**
 * Drone registration service exception class
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
public class DroneRegistrationException extends RuntimeException {

    public DroneRegistrationException(String msg) {
        super(msg);
    }
}
