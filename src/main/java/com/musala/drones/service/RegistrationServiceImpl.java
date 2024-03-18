package com.musala.drones.service;

import com.musala.drones.datamodel.data.Drone;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.DroneState;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.DroneRegistrationException;
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
public class RegistrationServiceImpl implements RegistrationService {

    private CacheService cacheService;
    private DroneRepository droneRepository;

    public RegistrationServiceImpl(CacheService cacheService, DroneRepository droneRepository) {
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
     * Map responseDTO
     *
     * @param requestDTO
     * @return response DTO
     */
    private ResponseDTO getResponseDTO(DroneRequestDTO requestDTO) {
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
