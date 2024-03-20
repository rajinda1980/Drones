package com.musala.drones.service;

import com.musala.drones.dto.DroneDTO;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.DroneRegistrationException;
import com.musala.drones.exception.DroneSearchException;

/**
 * A service interface to expose methods
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
public interface DroneService {

    public ResponseDTO registerDrone(DroneRequestDTO requestDTO) throws DroneRegistrationException;
    public DroneDTO getDrone(String sn) throws DroneSearchException;
}
