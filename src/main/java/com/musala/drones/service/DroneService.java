package com.musala.drones.service;

import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.DroneStatusChangeRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.DroneRegistrationException;
import com.musala.drones.exception.DroneSearchException;
import com.musala.drones.exception.DroneStatusException;

/**
 * A service interface to drone related services
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
public interface DroneService {

    ResponseDTO registerDrone(DroneRequestDTO requestDTO) throws DroneRegistrationException;
    ResponseDTO getDrone(String sn) throws DroneSearchException;
    ResponseDTO changeStatus(DroneStatusChangeRequestDTO droneStatusChangeRequestDTO) throws DroneStatusException;
}
