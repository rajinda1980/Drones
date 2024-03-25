package com.musala.drones.service;

import com.musala.drones.datamodel.data.Drone;
import com.musala.drones.datamodel.data.Medication;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.datamodel.repository.MedicationRepository;
import com.musala.drones.dto.*;
import com.musala.drones.exception.LoadMedicationException;
import com.musala.drones.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A service class implementation
 *
 * @author Rajinda
 * @version 1.0
 * @since 20/03/2024
 */
@Service
@Slf4j
public class MedicationServiceImpl implements MedicationService {

    private DroneRepository droneRepository;
    private MedicationRepository medicationRepository;
    private CacheService cacheService;

    public MedicationServiceImpl(DroneRepository droneRepository, MedicationRepository medicationRepository, CacheService cacheService) {
        this.droneRepository = droneRepository;
        this.medicationRepository = medicationRepository;
        this.cacheService = cacheService;
    }

    /**
     * Load medication to given drone
     *
     * @param medicationRequestDTO
     * @return ResponseDTO
     * @throws LoadMedicationException
     */
    @Transactional
    public ResponseDTO loadMedication(MedicationRequestDTO medicationRequestDTO) throws LoadMedicationException {
        try {
            Optional<Drone> optDrone = droneRepository.findById(medicationRequestDTO.getSerialNumber());

            if (optDrone.isPresent()) {
                Drone drone = optDrone.get();
                if (drone.getWeight() < medicationRequestDTO.getWeight()) {
                    log.error("Unable to load medication onto the drone due to overweight. The item weight should be " +
                            "less than or equal to {} grams", drone.getWeight());
                    throw new LoadMedicationException(AppConstants.DRONE_OVERWEIGHT + drone.getWeight() + " grams");

                } else if (drone.getCapacity() <= 25) {
                    log.error("Unable to load medication onto the drone due to battery capacity. The battery capacity should be " +
                            "greater than or equal to 25. Drone capacity is {}", drone.getCapacity());
                    throw new LoadMedicationException(AppConstants.LOW_CAPACITY);

                } else if (!drone.getState().getStatus().equals(DroneState.IDLE.name())) {
                    log.error("Unable to load medication onto the drone since it is not occupied. Please select another Drone." +
                            " Selected Drone Serial Number is {}", medicationRequestDTO.getSerialNumber());
                    throw new LoadMedicationException(AppConstants.DRONE_NOT_OCCUPIED);
                }

                Medication medication = new Medication();
                medication.setName(medicationRequestDTO.getName());
                medication.setWeight(medicationRequestDTO.getWeight());
                medication.setCode(medicationRequestDTO.getCode());
                medication.setImage(medicationRequestDTO.getImage());
                medication.setDrone(drone);

                drone.setState(cacheService.getDroneStates().get(DroneState.LOADING.name()));

                medicationRepository.saveAndFlush(medication);
                droneRepository.saveAndFlush(drone);


                ResponseDTO responseDTO =
                        getResponseDTO(
                                new MRequestWithoutImageDTO(
                                        medicationRequestDTO.getName(),
                                        medicationRequestDTO.getWeight(),
                                        medicationRequestDTO.getCode(),
                                        medicationRequestDTO.getSerialNumber()),
                                AppConstants.LOAD_MEDICATION_SUCCESS);
                return responseDTO;

            } else {
                log.info("Unable to load medication onto the drone due to the non-existent drone. Serial number : {}",
                        medicationRequestDTO.getSerialNumber());
                throw new LoadMedicationException(AppConstants.DRONE_DOES_NOT_EXIST + medicationRequestDTO.getSerialNumber());
            }

        } catch (Exception exception) {
            log.error("Exception when load medication to drone. Drone serial number : {}", medicationRequestDTO.getSerialNumber());
            throw new LoadMedicationException(exception.getMessage());
        }
    }

    /**
     * Return medication items to given serial number
     *
     * @param serialNumber - Drone serial number
     * @return medication item list
     * @throws LoadMedicationException
     */
    public List<LoadedMedicationItemDTO> findLoadedMedicationItems(String serialNumber) throws LoadMedicationException {
        try {
            Optional<List<Medication>> optMedications = medicationRepository.findAllByDrone(serialNumber);
            if (optMedications.isPresent()) {
                List<Medication> medications = optMedications.get();
                List<LoadedMedicationItemDTO> dtoList =
                        medications.stream()
                                .map(
                                        m -> {
                                            LoadedMedicationItemDTO item =
                                                    new LoadedMedicationItemDTO(serialNumber, m.getName(), m.getCode(), m.getWeight(), m.getImage());
                                            return item;
                                        }
                                ).collect(Collectors.toList());
                return dtoList;

            } else {
                throw new LoadMedicationException(AppConstants.NO_MEDICATION_ITEMS_FOUND + serialNumber);
            }

        } catch (Exception exception) {
            log.error("Exception when searching loaded medication items to given drone. Drone serial number : {}", serialNumber);
            throw new LoadMedicationException(exception.getMessage());
        }
    }

    /**
     * Map responseDTO
     *
     * @param requestDTO
     * @param message to user
     * @return response DTO
     */
    private ResponseDTO getResponseDTO(MRequestWithoutImageDTO requestDTO, String message) {
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
