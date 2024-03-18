package com.musala.drones.service;

import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.DroneRegistrationException;

/**
 * A service interface to expose methods
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
public interface RegistrationService {

    public ResponseDTO registerDrone(DroneRequestDTO requestDTO) throws DroneRegistrationException;
}
