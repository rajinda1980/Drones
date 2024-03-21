package com.musala.drones.service;

import com.musala.drones.datamodel.data.Drone;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.dto.*;
import com.musala.drones.exception.DroneRegistrationException;
import com.musala.drones.exception.DroneSearchException;
import com.musala.drones.exception.DroneStatusException;
import com.musala.drones.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A service class implementation
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@Service
@Slf4j
public class DroneServiceImpl implements DroneService {

    private CacheService cacheService;
    private DroneRepository droneRepository;

    public DroneServiceImpl(CacheService cacheService, DroneRepository droneRepository) {
        this.cacheService = cacheService;
        this.droneRepository = droneRepository;
    }

    /**
     * Registering drone information in the database
     *
     * @param requestDTO
     * @return responseDTO
     * @throws DroneRegistrationException
     */
    public ResponseDTO registerDrone(DroneRequestDTO requestDTO) throws DroneRegistrationException {
        try {
            Optional<Drone> dbIns = droneRepository.findById(requestDTO.getSerialNumber());
            if (dbIns.isPresent()) {
                throw new DroneRegistrationException(AppConstants.DRONE_REGISTERED_EXCEPTION);
            }

            Drone drone =
                    new Drone(
                            requestDTO.getSerialNumber(),
                            cacheService.getDroneModels().get(requestDTO.getModel()),
                            requestDTO.getWeight(),
                            requestDTO.getCapacity(),
                            cacheService.getDroneStates().get(DroneState.IDLE.name())
                    );
            droneRepository.saveAndFlush(drone);
            log.info("Drone registration is completed. Serial number : {}", requestDTO.getSerialNumber());
            return getResponseDTO(requestDTO);

        } catch (Exception e) {
            log.error("Drone registration cannot be completed. {}", e.getMessage());
            throw new DroneRegistrationException(e.getMessage());
        }
    }

    /**
     * Find drone record to given serial number
     *
     * @param sn - Serial number
     * @return droneDTO
     * @throws DroneSearchException
     */
    public DroneDTO getDrone(String sn) throws DroneSearchException {
        try {
            Optional<Drone> optDrone = droneRepository.findById(sn);
            if (!optDrone.isPresent()) {
                throw new DroneSearchException(AppConstants.DRONE_DOES_NOT_EXIST + sn);
            }

            Drone drone = optDrone.get();
            DroneDTO droneDTO = getDroneDTO(drone);
            return droneDTO;

        } catch (DroneSearchException exception) {
            log.error("Drone search exception. Drone Serial Number {}. {}", sn, exception.getMessage());
            throw new DroneRegistrationException(exception.getMessage());
        }
    }

    /**
     * Map Drone to DTO
     *
     * @param drone
     * @return DroneDTO
     */
    private DroneDTO getDroneDTO(Drone drone) {
        DroneDTO droneDTO =
                new DroneDTO(
                        drone.getSerialNumber(),
                        drone.getModel().getCategory(),
                        drone.getWeight(),
                        drone.getCapacity(),
                        drone.getState().getStatus()
                );
        return droneDTO;
    }

    /**
     * Change drone status
     *
     * @param droneStatusChangeRequestDTO
     * @return response DTO
     * @throws DroneStatusException
     */
    public ResponseDTO changeStatus(DroneStatusChangeRequestDTO droneStatusChangeRequestDTO) throws DroneStatusException {
        Optional<Drone> optDrone = droneRepository.findById(droneStatusChangeRequestDTO.getSerialNumber());
        if (!optDrone.isPresent()) {
            throw new DroneStatusException(AppConstants.DRONE_DOES_NOT_EXIST + droneStatusChangeRequestDTO.getSerialNumber());
        }

        Drone drone = optDrone.get();
        drone.setState(cacheService.getDroneStates().get(droneStatusChangeRequestDTO.getStatus()));
        droneRepository.saveAndFlush(drone);

        ResponseDTO responseDTO = getResponseDTO(droneStatusChangeRequestDTO);
        return responseDTO;
    }

    /**
     * Map responseDTO
     *
     * @param requestDTO
     * @return response DTO
     */
    private ResponseDTO getResponseDTO(Object requestDTO) {
        ResponseDTO responseDTO =
                new ResponseDTO(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        HttpStatus.OK.getReasonPhrase(),
                        AppConstants.DRONE_REGISTERED,
                        (Object) requestDTO
                );
        return responseDTO;
    }
}
