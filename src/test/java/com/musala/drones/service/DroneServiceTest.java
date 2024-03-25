package com.musala.drones.service;

import com.musala.drones.datamodel.data.Drone;
import com.musala.drones.datamodel.data.Model;
import com.musala.drones.datamodel.data.State;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.dto.DroneDTO;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.DroneStatusChangeRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.DroneRegistrationException;
import com.musala.drones.exception.DroneSearchException;
import com.musala.drones.exception.DroneStatusException;
import com.musala.drones.util.AppConstants;
import com.musala.drones.utils.TestConstants;
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
 * Junit test class to test Drone related functionalities
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
        Mockito.when(cacheService.getDroneModels()).thenReturn(TestConstants.loadModels());
        Mockito.when(cacheService.getDroneStates()).thenReturn(TestConstants.loadStatus());
        DroneRequestDTO requestDTO = getRequestDTO();
        Mockito.when(droneRepository.findById(requestDTO.getSerialNumber())).thenReturn(Optional.empty());

        ResponseDTO responseDTO = droneService.registerDrone(requestDTO);

        DroneRequestDTO serviceRequestDTO = (DroneRequestDTO) responseDTO.getObject();
        Assertions.assertEquals(requestDTO.getSerialNumber(), serviceRequestDTO.getSerialNumber());
        Assertions.assertEquals(requestDTO.getModel(), serviceRequestDTO.getModel());
        Assertions.assertEquals(requestDTO.getWeight(), serviceRequestDTO.getWeight());
        Assertions.assertEquals(requestDTO.getCapacity(), serviceRequestDTO.getCapacity());

        Assertions.assertNotNull(responseDTO.getCreatedDate());
        Assertions.assertEquals(HttpStatus.OK.value(), responseDTO.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getStatusValue());
        Assertions.assertEquals(AppConstants.DRONE_REGISTERED, responseDTO.getMessage());
        Assertions.assertTrue(responseDTO.getObject() instanceof DroneRequestDTO);
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

        Assertions.assertEquals(AppConstants.DRONE_REGISTERED_EXCEPTION, exception.getMessage());
    }

    @Test
    @DisplayName("Test the functionality to get drone - success path")
    void testGetDrone_success() throws Exception {
        Mockito.when(droneRepository.findById("D0001")).thenReturn(Optional.of(getDrone()));
        ResponseDTO responseDTO = droneService.getDrone("D0001");
        DroneDTO droneDTO = (DroneDTO) responseDTO.getObject();

        Assertions.assertNotNull(responseDTO.getCreatedDate());
        Assertions.assertEquals(HttpStatus.OK.value(), responseDTO.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getStatusValue());
        Assertions.assertEquals(AppConstants.DRONE_INFO, responseDTO.getMessage());

        Assertions.assertEquals("D0001", droneDTO.getSerialNumber());
        Assertions.assertEquals("Middleweight", droneDTO.getModel());
        Assertions.assertEquals("IDLE", droneDTO.getStatus());
        Assertions.assertEquals(150, droneDTO.getWeight());
        Assertions.assertEquals(100, droneDTO.getCapacity());
    }

    @Test
    @DisplayName("Test the functionality to get drone - Exception if drone does not exist")
    void testGetDrone_droneNotExist() throws Exception {
        Mockito.when(droneRepository.findById("S0001")).thenReturn(Optional.empty());
        DroneSearchException exception =
                Assertions.assertThrows(DroneSearchException.class,
                        () -> droneService.getDrone("S0001"));
        Assertions.assertEquals(AppConstants.DRONE_DOES_NOT_EXIST + "S0001", exception.getMessage());
    }

    @Test
    @DisplayName("Test the functionality to change drone status - happy path")
    void testChangeStatus_success() throws Exception {
        Mockito.when(cacheService.getDroneStates()).thenReturn(TestConstants.loadStatus());
        Drone drone = getDrone();
        Mockito.when(droneRepository.findById("D0001")).thenReturn(Optional.of(drone));

        DroneStatusChangeRequestDTO requestDTO =
                new DroneStatusChangeRequestDTO("D0001", "LOADED");
        ResponseDTO responseDTO = droneService.changeStatus(requestDTO);

        Assertions.assertNotNull(responseDTO.getCreatedDate());
        Assertions.assertEquals(HttpStatus.OK.value(), responseDTO.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getStatusValue());
        Assertions.assertEquals(AppConstants.DRONE_STATUS_CHANGE_SUCCESS + "D0001", responseDTO.getMessage());
        Assertions.assertTrue(responseDTO.getObject() instanceof DroneStatusChangeRequestDTO);

        DroneStatusChangeRequestDTO object = (DroneStatusChangeRequestDTO) responseDTO.getObject();
        Assertions.assertEquals("D0001", object.getSerialNumber());
        Assertions.assertEquals("LOADED", object.getStatus());

        Assertions.assertEquals(cacheService.getDroneStates().get("LOADED"), drone.getState());
    }

    @Test
    @DisplayName("Test the functionality to change drone status - Drone does not exist")
    void testChangeStatus_exception() throws Exception {
        Mockito.when(droneRepository.findById("D0001")).thenReturn(Optional.empty());
        DroneStatusChangeRequestDTO requestDTO = new DroneStatusChangeRequestDTO("D0001", "LOADED");

        DroneStatusException exception =
                Assertions.assertThrows(
                        DroneStatusException.class,
                        () -> droneService.changeStatus(requestDTO)
                );
        Assertions.assertEquals(AppConstants.DRONE_DOES_NOT_EXIST + requestDTO.getSerialNumber(), exception.getMessage());
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
