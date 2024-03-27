package com.musala.drones.service;

import com.musala.drones.datamodel.data.Drone;
import com.musala.drones.datamodel.data.DroneAudit;
import com.musala.drones.datamodel.repository.DroneAuditRepository;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.dto.*;
import com.musala.drones.exception.DroneRegistrationException;
import com.musala.drones.exception.DroneSearchException;
import com.musala.drones.exception.DroneStatusException;
import com.musala.drones.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private DroneAuditRepository droneAuditRepository;

    public DroneServiceImpl(CacheService cacheService, DroneRepository droneRepository, DroneAuditRepository droneAuditRepository) {
        this.cacheService = cacheService;
        this.droneRepository = droneRepository;
        this.droneAuditRepository = droneAuditRepository;
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
            return getResponseDTO(requestDTO, AppConstants.DRONE_REGISTERED);

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
    public ResponseDTO getDrone(String sn) throws DroneSearchException {
        try {
            Optional<Drone> optDrone = droneRepository.findById(sn);
            if (!optDrone.isPresent()) {
                throw new DroneSearchException(AppConstants.DRONE_DOES_NOT_EXIST + sn);
            }

            Drone drone = optDrone.get();
            DroneDTO droneDTO = getDroneDTO(drone);
            return getResponseDTO(droneDTO, AppConstants.DRONE_INFO);

        } catch (Exception exception) {
            log.error("Drone search exception. Drone Serial Number {}. {}", sn, exception.getMessage());
            throw new DroneSearchException(exception.getMessage());
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
        try {
            Optional<Drone> optDrone = droneRepository.findById(droneStatusChangeRequestDTO.getSerialNumber());
            if (!optDrone.isPresent()) {
                throw new DroneStatusException(AppConstants.DRONE_DOES_NOT_EXIST + droneStatusChangeRequestDTO.getSerialNumber());
            }

            Drone drone = optDrone.get();
            drone.setState(cacheService.getDroneStates().get(droneStatusChangeRequestDTO.getStatus()));
            droneRepository.saveAndFlush(drone);

            ResponseDTO responseDTO = getResponseDTO(droneStatusChangeRequestDTO,
                    AppConstants.DRONE_STATUS_CHANGE_SUCCESS + droneStatusChangeRequestDTO.getSerialNumber());
            return responseDTO;

        } catch (Exception exception) {
            log.error("Drone status change exception. Serial number : {}, Exception : {}",
                    droneStatusChangeRequestDTO.getSerialNumber(), exception.getMessage());
            throw new DroneStatusException(exception.getMessage());
        }
    }

    /**
     * Search drones in idle status
     *
     * @return available drone list
     * @throws DroneSearchException
     */
    public List<AvailableDroneDTO> findIdleDrones() throws DroneSearchException {
        try {
            Optional<List<Drone>> optDrones = droneRepository.findAllByState("IDLE");
            if (optDrones.isPresent() && optDrones.get().size() > 0) {
                List<Drone> drones = optDrones.get();
                List<AvailableDroneDTO> availableDroneDTOS =
                        drones.stream()
                                .map(
                                        d -> {
                                            AvailableDroneDTO dto =
                                                    new AvailableDroneDTO(d.getSerialNumber(), d.getModel().getCategory(), d.getWeight(), d.getCapacity());
                                            return dto;
                                        }
                                ).collect(Collectors.toList());
                return availableDroneDTOS;

            } else {
                log.info("No available drones found");
                throw new DroneSearchException(AppConstants.NO_AVAILABLE_DRONE_FOUND);
            }

        } catch (Exception exception) {
            log.error("Exception when searching drones in IDLE status. {}", exception.getMessage());
            throw new DroneSearchException(exception.getMessage());
        }
    }

    /**
     * Find drone battery level
     *
     * @param serialNumber
     * @return drone battery level
     * @throws DroneSearchException
     */
    public Integer getDroneBatteryLevel(String serialNumber) throws DroneSearchException {
        try {
            Optional<Drone> optDrone = droneRepository.findById(serialNumber);
            if (optDrone.isPresent()) {
                return optDrone.get().getCapacity();
            } else {
                log.info("Drone does not exist to given serial number. Drone Serial Number : {}", serialNumber);
                throw new DroneSearchException(AppConstants.DRONE_DOES_NOT_EXIST + serialNumber);
            }

        } catch (Exception exception) {
            log.error("Exception when searching drones battery level. Drone serial number : {}, Exception : {}",
                    serialNumber, exception.getMessage());
            throw new DroneSearchException(exception.getMessage());
        }
    }

    /**
     * Schedule check of battery level check
     *
     * @throws Exception
     */
    @Transactional
    public void checkDroneBatteryLevel() throws Exception {
        List<Drone> drones = droneRepository.findAll();

        if (drones.isEmpty()) {
            throw new Exception(AppConstants.SCHEDULE_NO_DRONE_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String auditId = now.format(pattern);

        List<DroneAudit> droneAuditList =
                drones.stream()
                        .map(
                                d -> {
                                    DroneAudit audit = new DroneAudit(auditId, d.getSerialNumber(), d.getCapacity(), LocalDateTime.now());
                                    return audit;
                                }
                        ).collect(Collectors.toList());

        droneAuditRepository.saveAll(droneAuditList);
    }

    /**
     * Map responseDTO
     *
     * @param requestDTO
     * @return response DTO
     */
    private ResponseDTO getResponseDTO(Object requestDTO, String message) {
        ResponseDTO responseDTO =
                new ResponseDTO(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        HttpStatus.OK.getReasonPhrase(),
                        message,
                        (Object) requestDTO
                );
        return responseDTO;
    }
}
