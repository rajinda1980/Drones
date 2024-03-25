package com.musala.drones.service;

import com.musala.drones.datamodel.data.Drone;
import com.musala.drones.datamodel.data.Model;
import com.musala.drones.datamodel.data.State;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.datamodel.repository.MedicationRepository;
import com.musala.drones.dto.MRequestWithoutImageDTO;
import com.musala.drones.dto.MedicationRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.LoadMedicationException;
import com.musala.drones.util.AppConstants;
import com.musala.drones.utils.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Junit test class to test medication related functionalities
 *
 * @author Rajinda
 * @version 1.0
 * @since 23/03/2024
 */
@ExtendWith(MockitoExtension.class)
public class MedicationServiceTest {

    @InjectMocks
    MedicationServiceImpl medicationService;

    @Mock
    MedicationRepository medicationRepository;

    @Mock
    DroneRepository droneRepository;

    @Mock
    CacheServiceImpl cacheService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test load medications - happy path")
    void loadMedication_success() throws Exception {
        Mockito.when(cacheService.getDroneModels()).thenReturn(TestConstants.loadModels());
        Mockito.when(cacheService.getDroneStates()).thenReturn(TestConstants.loadStatus());

        Drone drone = new Drone("SU1002", cacheService.getDroneModels().get("Middleweight"), 350, 90,
                cacheService.getDroneStates().get("IDLE"));
        Mockito.when(droneRepository.findById("SU1002")).thenReturn(Optional.of(drone));

        MedicationRequestDTO requestDTO =
                new MedicationRequestDTO("MED-0001", 350, "CD_45445", TestConstants.getImage("PNG"), "SU1002");

        ResponseDTO responseDTO = medicationService.loadMedication(requestDTO);

        Assertions.assertNotNull(responseDTO.getCreatedDate());
        Assertions.assertEquals(HttpStatus.OK.value(), responseDTO.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), responseDTO.getStatusValue());
        Assertions.assertEquals(AppConstants.LOAD_MEDICATION_SUCCESS, responseDTO.getMessage());

        MRequestWithoutImageDTO withoutImageDTO = (MRequestWithoutImageDTO) responseDTO.getObject();
        Assertions.assertEquals(requestDTO.getName(), withoutImageDTO.getName());
        Assertions.assertEquals(requestDTO.getCode(), withoutImageDTO.getCode());
        Assertions.assertEquals(requestDTO.getWeight(), withoutImageDTO.getWeight());
        Assertions.assertEquals(requestDTO.getSerialNumber(), withoutImageDTO.getSerialNumber());

        Assertions.assertEquals(cacheService.getDroneStates().get("LOADING"), drone.getState());
    }

    /**
     * Value map
     *
     * @return Arguments
     * @throws Exception
     */
    static Stream<Arguments> loadMedication_exceptions() throws Exception {
        MedicationRequestDTO requestDTO_weight =
                new MedicationRequestDTO("MED-0001", 500, "CD_45445", TestConstants.getImage("PNG"), "SU1002");
        Drone drone_weight = new Drone("SU1002", new Model(1L, "Middleweight"), 350, 20, new State(1L, "LOADING"));
        String expected_weight = AppConstants.DRONE_OVERWEIGHT + "350 grams";

        MedicationRequestDTO requestDTO_cap =
                new MedicationRequestDTO("MED-0001", 500, "CD_45445", TestConstants.getImage("PNG"), "SU1002");
        Drone drone_cap = new Drone("SU1002", new Model(1L, "Middleweight"), 500, 20, new State(1L, "LOADING"));
        String expected_cap = AppConstants.LOW_CAPACITY;

        MedicationRequestDTO requestDTO_state =
                new MedicationRequestDTO("MED-0001", 500, "CD_45445", TestConstants.getImage("PNG"), "SU1002");
        Drone drone_state = new Drone("SU1002", new Model(1L, "Middleweight"), 500, 80, new State(1L, "LOADING"));
        String expected_state = AppConstants.DRONE_NOT_OCCUPIED;

        return Stream.of(
                Arguments.of(requestDTO_weight, drone_weight, expected_weight),
                Arguments.of(requestDTO_cap, drone_cap, expected_cap),
                Arguments.of(requestDTO_state, drone_state, expected_state)
        );
    }

    @ParameterizedTest
    @DisplayName("Test load medications - Exceptions")
    @MethodSource
    void loadMedication_exceptions(MedicationRequestDTO requestDTO, Drone drone, String expected) throws Exception {
        Mockito.when(droneRepository.findById("SU1002")).thenReturn(Optional.of(drone));

        LoadMedicationException exception =
                Assertions.assertThrows(LoadMedicationException.class,
                        () -> medicationService.loadMedication(requestDTO));
        Assertions.assertEquals(expected, exception.getMessage());
    }

}
