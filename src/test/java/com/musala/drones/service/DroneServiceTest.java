package com.musala.drones.service;

import com.musala.drones.datamodel.data.Drone;
import com.musala.drones.datamodel.data.Model;
import com.musala.drones.datamodel.data.State;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.DroneRegistrationException;
import com.musala.drones.util.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * Junit test class to test Drone registration functionalities
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@ExtendWith(MockitoExtension.class)
public class DroneServiceTest {

    @InjectMocks
    DroneServiceImpl droneService;

    @Mock
    DroneRepository droneRepository;

    @Mock
    CacheServiceImpl cacheService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test the functionality for registering a drone - Happy path")
    void testRegisterDrone_success() throws Exception {
        Drone drone = getDrone();
        DroneRequestDTO requestDTO = getRequestDTO();
        Mockito.when(droneRepository.findById(requestDTO.getSerialNumber())).thenReturn(Optional.empty());

        ResponseDTO responseDTO = droneService.registerDrone(requestDTO);
        Assertions.assertNotNull(responseDTO.getCreatedDate());
        Assertions.assertEquals(responseDTO.getStatusCode(), HttpStatus.OK.value());
        Assertions.assertEquals(responseDTO.getStatusValue(), HttpStatus.OK.getReasonPhrase());
        Assertions.assertEquals(responseDTO.getMessage(), AppConstants.DRONE_REGISTERED);
        Assertions.assertTrue(responseDTO.getObject() instanceof DroneRequestDTO);

        DroneRequestDTO serviceRequestDTO = (DroneRequestDTO) responseDTO.getObject();
        Assertions.assertEquals(serviceRequestDTO.getSerialNumber(), requestDTO.getSerialNumber());
        Assertions.assertEquals(serviceRequestDTO.getModel(), requestDTO.getModel());
        Assertions.assertEquals(serviceRequestDTO.getWeight(), requestDTO.getWeight());
        Assertions.assertEquals(serviceRequestDTO.getCapacity(), requestDTO.getCapacity());
    }

    @Test
    @DisplayName("Test the functionality for registering a drone - Exception path. Prerequisites : Drone is registered to given serial number")
    void testRegisterDrone_DroneExist() throws Exception {
        Drone drone = getDrone();
        DroneRequestDTO requestDTO = getRequestDTO();
        Mockito.when(droneRepository.findById(requestDTO.getSerialNumber())).thenReturn(Optional.of(drone));

        DroneRegistrationException exception =
                Assertions.assertThrows(DroneRegistrationException.class,
                        () -> droneService.registerDrone(requestDTO));

        Assertions.assertEquals(exception.getMessage(), AppConstants.DRONE_REGISTERED_EXCEPTION);
    }

    private Drone getDrone() {
        Model model = new Model(5L, "Middleweight");
        State state = new State(1L, "IDLE");
        Drone drone = new Drone("D0001", model, 150, 100, state);
        return drone;
    }

    private DroneRequestDTO getRequestDTO() {
        DroneRequestDTO requestDTO = new DroneRequestDTO("D001", "Middleweight", 150, 100);
        return requestDTO;
    }
}
