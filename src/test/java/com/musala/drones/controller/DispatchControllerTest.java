package com.musala.drones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.musala.drones.dto.*;
import com.musala.drones.exception.DroneRegistrationException;
import com.musala.drones.exception.DroneSearchException;
import com.musala.drones.exception.DroneStatusException;
import com.musala.drones.exception.LoadMedicationException;
import com.musala.drones.service.CacheService;
import com.musala.drones.service.DroneService;
import com.musala.drones.service.MedicationService;
import com.musala.drones.util.AppConstants;
import com.musala.drones.utils.TestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;

/**
 * Junit test class to test controller
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DispatchControllerTest {

    @MockBean
    DroneService droneService;

    @MockBean
    MedicationService medicationService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CacheService cacheService;

    private String droneRegistrationURL;
    private String droneGetURL;
    private String loadMedication;
    private String droneStatusChange;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        droneRegistrationURL = TestConstants.REGISTER_DRONE_URL;
        droneGetURL = TestConstants.DRONE_GET_URL;
        loadMedication = TestConstants.LOAD_MEDICATION_URL;
        droneStatusChange = TestConstants.DRONE_STATUS_CHANGE_URL;
    }

    @Test
    @DisplayName("Unit Test => Test the functionality for registering a drone - Happy path")
    void testRegisterDrone_success() throws Exception {
        ResponseDTO responseDTO = getDroneResponseDTO_success();
        DroneRequestDTO requestDTO = getDroneRequestDTO();

        Mockito.when(droneService.registerDrone(isA(DroneRequestDTO.class))).thenReturn(responseDTO);
        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(droneRegistrationURL)
                                .contentType(AppConstants.CONTENT_TYPE)
                                .content(getStringObject(requestDTO))
                ).andReturn();

        Gson gson = TestConstants.getFullyFledgedGson();
        ResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ResponseDTO.class);

        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), response.getStatusValue());
        Assertions.assertEquals(AppConstants.DRONE_REGISTERED, response.getMessage());

        Map fromResponse = (Map) response.getObject();
        Assertions.assertEquals(requestDTO.getSerialNumber(), fromResponse.get("serialNumber"));
        Assertions.assertEquals(requestDTO.getModel(), fromResponse.get("model"));
        Assertions.assertEquals(requestDTO.getWeight().toString(), fromResponse.get("weight").toString());
        Assertions.assertEquals(requestDTO.getCapacity().toString(), fromResponse.get("capacity").toString());
    }

    /**
     * Value map
     * @throws Exception
     */
    static Stream<Arguments> testRegisterDrone_Exception() throws Exception {
        DroneRequestDTO requestDTO_SNMin = new DroneRequestDTO("", "Lightweight", 100, 50);
        List detailsMin = new ArrayList();
        ErrorDetailDTO errorDetailMin = new ErrorDetailDTO();
        errorDetailMin.setFieldName("serialNumber");
        errorDetailMin.setFieldValue("");
        errorDetailMin.setMessage(AppConstants.SERIAL_NUMBER_LENGTH_EXCEED);
        detailsMin.add(errorDetailMin);
        ErrorResponseDTO responseDTO_SNMin = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), detailsMin, "/v1/api/drone/register");

        DroneRequestDTO requestDTO_SNMax = new DroneRequestDTO("D1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_",
                "Middleweight", 250, 75);
        List detailsMax = new ArrayList();
        ErrorDetailDTO errorDetailMax = new ErrorDetailDTO();
        errorDetailMax.setFieldName("serialNumber");
        errorDetailMax.setFieldValue("D1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_");
        errorDetailMax.setMessage(AppConstants.SERIAL_NUMBER_LENGTH_EXCEED);
        detailsMax.add(errorDetailMax);
        ErrorResponseDTO responseDTO_SNMax = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), detailsMax, "/v1/api/drone/register");

        DroneRequestDTO requestDTO_weight_low = new DroneRequestDTO("D102030", "Middleweight", 0, 100);
        List detailsWeightLow = new ArrayList();
        ErrorDetailDTO errorDetailWeightLow = new ErrorDetailDTO();
        errorDetailWeightLow.setFieldName("weight");
        errorDetailWeightLow.setFieldValue("0.0");
        errorDetailWeightLow.setMessage(AppConstants.DRONE_WEIGHT_LOW);
        detailsWeightLow.add(errorDetailWeightLow);
        ErrorResponseDTO responseDTO_weight_low = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), detailsWeightLow, "/v1/api/drone/register");

        DroneRequestDTO requestDTO_weight_high = new DroneRequestDTO("D102030", "Middleweight", 750, 100);
        List detailsWeightHigh = new ArrayList();
        ErrorDetailDTO errorDetailWeightHigh = new ErrorDetailDTO();
        errorDetailWeightHigh.setFieldName("weight");
        errorDetailWeightHigh.setFieldValue("750.0");
        errorDetailWeightHigh.setMessage(AppConstants.DRONE_WEIGHT_EXCEEDED);
        detailsWeightHigh.add(errorDetailWeightHigh);
        ErrorResponseDTO responseDTO_weight_high = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), detailsWeightHigh, "/v1/api/drone/register");

        DroneRequestDTO requestDTO_capacity_low = new DroneRequestDTO("D102030", "Middleweight", 250, 0);
        List detailsCapacityLow = new ArrayList();
        ErrorDetailDTO errorDetailCapacityLow = new ErrorDetailDTO();
        errorDetailCapacityLow.setFieldName("capacity");
        errorDetailCapacityLow.setFieldValue("0.0");
        errorDetailCapacityLow.setMessage(AppConstants.BATTERY_CAPACITY_LOW);
        detailsCapacityLow.add(errorDetailCapacityLow);
        ErrorResponseDTO responseDTO_capacity_low = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), detailsCapacityLow, "/v1/api/drone/register");

        DroneRequestDTO requestDTO_capacity_high = new DroneRequestDTO("D102030", "Middleweight", 250, 150);
        List detailsCapacityHigh = new ArrayList();
        ErrorDetailDTO errorDetailCapacityHigh = new ErrorDetailDTO();
        errorDetailCapacityHigh.setFieldName("capacity");
        errorDetailCapacityHigh.setFieldValue("150.0");
        errorDetailCapacityHigh.setMessage(AppConstants.BATTERY_CAPACITY_EXCEEDED);
        detailsCapacityHigh.add(errorDetailCapacityHigh);
        ErrorResponseDTO responseDTO_capacity_high = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), detailsCapacityHigh, "/v1/api/drone/register");

        return Stream.of(
                Arguments.of(requestDTO_SNMin, responseDTO_SNMin),
                Arguments.of(requestDTO_SNMax, responseDTO_SNMax),
                Arguments.of(requestDTO_weight_low, responseDTO_weight_low),
                Arguments.of(requestDTO_weight_high, responseDTO_weight_high),
                Arguments.of(requestDTO_capacity_low, responseDTO_capacity_low),
                Arguments.of(requestDTO_capacity_high, responseDTO_capacity_high)
        );
    }

    @ParameterizedTest
    @DisplayName("Unit Test => Test the functionality for registering a drone - Exception scenario of DTO level validation")
    @MethodSource()
    void testRegisterDrone_Exception(DroneRequestDTO requestDTO, ErrorResponseDTO expected) throws Exception {
        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(droneRegistrationURL)
                                .contentType(AppConstants.CONTENT_TYPE)
                                .content(getStringObject(requestDTO))
                ).andReturn();

        Gson gson = TestConstants.getFullyFledgedGson();
        ErrorResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ErrorResponseDTO.class);

        Assertions.assertEquals(expected.getStatus(), result.getResponse().getStatus());
        Assertions.assertEquals(expected.getStatus(), response.getStatus());
        Assertions.assertTrue(response.getDetail().size() > 0);
        Assertions.assertEquals(TestConstants.REGISTER_DRONE_URL, response.getPath().toString());

        Map fromResponse = (Map) response.getDetail().get(0);
        ErrorDetailDTO expectedErrorDetail = (ErrorDetailDTO)  expected.getDetail().get(0);
        Assertions.assertEquals(expectedErrorDetail.getMessage(), fromResponse.get("message"));
        Assertions.assertEquals(expectedErrorDetail.getFieldName(), fromResponse.get("fieldName"));
        Assertions.assertEquals(expectedErrorDetail.getFieldValue(), fromResponse.get("fieldValue").toString());
    }

    @Test
    @DisplayName("Unit Test => Test functionality for invalid drone model")
    void testRegisterDrone_InvalidModel() throws Exception {
        DroneRequestDTO requestDTO_mode = new DroneRequestDTO("D102030", "Mediumweight", 250, 50);
        List detailsMode = new ArrayList();
        ErrorDetailHeaderDTO errorDetailMode = new ErrorDetailHeaderDTO();
        errorDetailMode.setMessage(AppConstants.INVALID_MODEL + cacheService.getDroneModels().keySet().toString());
        detailsMode.add(errorDetailMode);
        ErrorResponseDTO expected = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), detailsMode, "/v1/api/drone/register");

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(droneRegistrationURL)
                                .contentType(AppConstants.CONTENT_TYPE)
                                .content(getStringObject(requestDTO_mode))
                ).andReturn();

        Gson gson = TestConstants.getFullyFledgedGson();
        ErrorResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ErrorResponseDTO.class);

        Assertions.assertEquals(expected.getStatus(), result.getResponse().getStatus());
        Assertions.assertEquals(expected.getStatus(), response.getStatus());
        Assertions.assertTrue(response.getDetail().size() > 0);

        Assertions.assertEquals(TestConstants.REGISTER_DRONE_URL, response.getPath().toString());
        Map fromResponse = (Map) response.getDetail().get(0);
        Assertions.assertEquals(errorDetailMode.getMessage(), fromResponse.get("message").toString());
    }

    @Test
    @DisplayName("Unit Test => Test get drone details functionality - Success path")
    void testGetDrone_success() throws Exception {
        ResponseDTO responseDTO = getDroneSearchResponseDTO_success("S0001");
        Mockito.when(droneService.getDrone(isA(String.class))).thenReturn(responseDTO);

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(TestConstants.DRONE_GET_URL + "S0001")
                                .contentType(AppConstants.CONTENT_TYPE)
                ).andReturn();

        Gson gson = TestConstants.getFullyFledgedGson();
        ResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ResponseDTO.class);

        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), response.getStatusValue());
        Assertions.assertEquals(AppConstants.DRONE_INFO, response.getMessage());

        Map fromResponse = (Map) response.getObject();
        Assertions.assertEquals("S0001", fromResponse.get("serialNumber"));
        Assertions.assertEquals(((DroneDTO) responseDTO.getObject()).getModel(), fromResponse.get("model"));
        Assertions.assertEquals(((DroneDTO) responseDTO.getObject()).getWeight().toString(), fromResponse.get("weight").toString());
        Assertions.assertEquals(((DroneDTO) responseDTO.getObject()).getCapacity().toString(), fromResponse.get("capacity").toString());
        Assertions.assertEquals(((DroneDTO) responseDTO.getObject()).getStatus(), fromResponse.get("status").toString());
    }

    @Test
    @DisplayName("Unit Test => Test get drone details functionality - Exception when drone does not exist")
    void testGetDrone_exception() throws Exception {
        Mockito.doThrow(new DroneSearchException(AppConstants.DRONE_DOES_NOT_EXIST + "S0001"))
                        .when(droneService).getDrone(isA(String.class));
        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(TestConstants.DRONE_GET_URL + "S0001")
                                .contentType(AppConstants.CONTENT_TYPE)
                ).andReturn();

        Assertions.assertEquals(result.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
        Gson gson = TestConstants.getFullyFledgedGson();
        ErrorResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ErrorResponseDTO.class);
        Assertions.assertEquals(AppConstants.DRONE_DOES_NOT_EXIST + "S0001", ((Map) response.getDetail().get(0)).get("message"));
    }

    @Test
    @DisplayName("Unit Test => Test register drone functionality - Exception when drone is already exist")
    void testRegisterDrone_exception() throws Exception {
        Mockito.doThrow(new DroneRegistrationException(AppConstants.DRONE_REGISTERED_EXCEPTION))
                .when(droneService).registerDrone(isA(DroneRequestDTO.class));
        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(droneRegistrationURL)
                                .contentType(AppConstants.CONTENT_TYPE)
                                .content(getStringObject(getDroneRequestDTO()))
                ).andReturn();

        Assertions.assertEquals(result.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
        Gson gson = TestConstants.getFullyFledgedGson();
        ErrorResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ErrorResponseDTO.class);
        Assertions.assertEquals(AppConstants.DRONE_REGISTERED_EXCEPTION, ((Map) response.getDetail().get(0)).get("message"));
    }

    @Test
    @DisplayName("Unit Test => Test the functionality for load medication - Happy path")
    void testLoadMedication() throws Exception {
        ResponseDTO responseDTO = getMedicationResponseDTO_success();
        MedicationRequestDTO requestDTO = getMedicationRequestDTO();
        when(medicationService.loadMedication(isA(MedicationRequestDTO.class))).thenReturn(responseDTO);

        MvcResult result =  mockMvc.perform(
                                MockMvcRequestBuilders
                                        .post(TestConstants.LOAD_MEDICATION_URL)
                                        .content(getStringObject((MedicationRequestDTO) responseDTO.getObject()))
                                        .contentType(AppConstants.CONTENT_TYPE))
                                .andReturn();

        Gson gson = TestConstants.getFullyFledgedGson();
        ResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ResponseDTO.class);

        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), response.getStatusValue());
        Assertions.assertEquals(AppConstants.LOAD_MEDICATION_SUCCESS, response.getMessage());

        Map fromResponse = (Map) response.getObject();
        Assertions.assertEquals("S0001", fromResponse.get("serialNumber"));
        Assertions.assertEquals(requestDTO.getName(), fromResponse.get("name"));
        Assertions.assertEquals(requestDTO.getWeight().toString(), fromResponse.get("weight").toString());
        Assertions.assertEquals(requestDTO.getCode(), fromResponse.get("code").toString());
        Assertions.assertEquals(requestDTO.getSerialNumber(), fromResponse.get("serialNumber").toString());
    }

    /**
     * Value map for loading medication
     *
     * @return Argument list
     * @throws Exception
     */
    static Stream<Arguments> testLoadMedication_Exceptions() throws Exception{
        String msg_weight = AppConstants.DRONE_OVERWEIGHT + " 400 grams";
        String msg_cap = AppConstants.LOW_CAPACITY;
        String msg_occ = AppConstants.DRONE_NOT_OCCUPIED;

        return Stream.of(
                Arguments.of(msg_weight),
                Arguments.of(msg_cap),
                Arguments.of(msg_occ)
        );
    }

    @ParameterizedTest
    @DisplayName("Unit Test => Test the functionality for load medication - An exception is thrown when the weight of the medication exceeds the weight capacity of the drone")
    @MethodSource
    void testLoadMedication_Exceptions(String msg) throws Exception {
        MedicationRequestDTO requestDTO = getMedicationRequestDTO();
        String errMessage = msg;
        Mockito.doThrow(new LoadMedicationException(errMessage)).when(medicationService).loadMedication(isA(MedicationRequestDTO.class));

        MvcResult result =  mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(TestConstants.LOAD_MEDICATION_URL)
                                .content(getStringObject(requestDTO))
                                .contentType(AppConstants.CONTENT_TYPE))
                        .andReturn();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        Gson gson = TestConstants.getFullyFledgedGson();
        ErrorResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ErrorResponseDTO.class);
        Assertions.assertEquals(errMessage, ((Map) response.getDetail().get(0)).get("message"));
    }

    @Test
    @DisplayName("Unit Test => Drone status change - happy path")
    void testChangeStatus_success() throws Exception {
        DroneStatusChangeRequestDTO requestDTO = getDroneStatusChangeRequestDTO();
        ResponseDTO responseDTO = getDroneStatusChangeResponseDTO_success();
        when(droneService.changeStatus(isA(DroneStatusChangeRequestDTO.class))).thenReturn(responseDTO);

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(TestConstants.DRONE_STATUS_CHANGE_URL)
                                .content(getStringObject(requestDTO))
                                .contentType(AppConstants.CONTENT_TYPE)
                        ).andReturn();

        Gson gson = TestConstants.getFullyFledgedGson();
        ResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ResponseDTO.class);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), response.getStatusValue());
        Assertions.assertEquals(AppConstants.DRONE_STATUS_CHANGE_SUCCESS + requestDTO.getSerialNumber(), response.getMessage());

        Map fromResponse = (Map) response.getObject();
        Assertions.assertEquals(requestDTO.getSerialNumber(), fromResponse.get("serialNumber"));
        Assertions.assertEquals(requestDTO.getStatus(), fromResponse.get("status"));
    }

    /**
     * Value map
     *
     * @return value map
     * @throws Exception
     */
    static Stream<Arguments> testChangeStatus_invalidStatus() throws Exception {
        String msg_is = AppConstants.INVALID_MODEL + "[IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING]";
        String msg_sn = AppConstants.DRONE_DOES_NOT_EXIST + "S0001";

        return Stream.of(
                Arguments.of(msg_is),
                Arguments.of(msg_sn)
        );
    }

    @ParameterizedTest
    @DisplayName("Unit Test => Drone status change - Exception scenarios")
    @MethodSource
    void testChangeStatus_invalidStatus(String msg) throws Exception {
        DroneStatusChangeRequestDTO requestDTO = getDroneStatusChangeRequestDTO();
        String errMessage = msg;
        Mockito.doThrow(new DroneStatusException(errMessage)).when(droneService).changeStatus(isA(DroneStatusChangeRequestDTO.class));

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(TestConstants.DRONE_STATUS_CHANGE_URL)
                                .content(getStringObject(requestDTO))
                                .contentType(AppConstants.CONTENT_TYPE)
                ).andReturn();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        Gson gson = TestConstants.getFullyFledgedGson();
        ErrorResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ErrorResponseDTO.class);
        Assertions.assertEquals(errMessage, ((Map) response.getDetail().get(0)).get("message"));
    }

    @Test
    @DisplayName("Unit Test => Test case for verifying loaded medication items controller method - Happy path")
    void testFindLoadedMedicationItems_success() throws Exception {
        Mockito.when(medicationService.findLoadedMedicationItems(isA(String.class))).thenReturn(getLoadedMedicationItems());

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(TestConstants.LOADED_MEDICATION_ITEM_URL + "2001")
                                .contentType(AppConstants.CONTENT_TYPE)
                ).andReturn();

        Gson gson = TestConstants.getFullyFledgedGson();
        List response = gson.fromJson(result.getResponse().getContentAsString(), List.class);
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Assertions.assertEquals(5, response.size());

        String base64String = Base64.getEncoder().encodeToString(TestConstants.getImage("PNG"));
        String imageJson = gson.toJson(base64String);
        // Remove first and last double quotes added by gson
        String imageOutput =  imageJson.substring(1, imageJson.length() - 1);

        for (int i = 0; i < 5; i++) {
            LinkedTreeMap mapItem = (LinkedTreeMap) response.get(i);
            Assertions.assertTrue(mapItem.get("serialNumber").toString().matches("2001"));
            Assertions.assertTrue(mapItem.get("name").toString().matches("^(UT5510-1420|UT5510-1421|UT5510-1422|UT5510-1423|UT5510-1424)$"));
            Assertions.assertTrue(mapItem.get("code").toString().matches("^(BT-4850|BT-4851|BT-4852|BT-4853|BT-4854)$"));
            Assertions.assertTrue(mapItem.get("weight").toString().matches("^(150|200|250|300|350)$"));
            Assertions.assertEquals(imageOutput, mapItem.get("image").toString());
        }
    }

    @Test
    @DisplayName("Unit Test => Test case for verifying loaded medication items controller method - exceptions")
    void testFindLoadedMedicationItems_exception() throws Exception {
        String errMessage = AppConstants.NO_MEDICATION_ITEMS_FOUND + "2001";

        Mockito.doThrow(new LoadMedicationException(AppConstants.NO_MEDICATION_ITEMS_FOUND + "2001"))
                .when(medicationService).findLoadedMedicationItems(isA(String.class));

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(TestConstants.LOADED_MEDICATION_ITEM_URL + "2001")
                                .contentType(AppConstants.CONTENT_TYPE)
                ).andReturn();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        Gson gson = TestConstants.getFullyFledgedGson();
        ErrorResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ErrorResponseDTO.class);
        Assertions.assertEquals(errMessage, ((Map) response.getDetail().get(0)).get("message"));
    }

    private ResponseDTO getDroneResponseDTO_success() throws Exception{
        ResponseDTO responseDTO =
                new ResponseDTO(LocalDateTime.now(), HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), AppConstants.DRONE_REGISTERED, getDroneRequestDTO());
        return responseDTO;
    }

    private DroneRequestDTO getDroneRequestDTO() throws Exception {
        return new DroneRequestDTO("D001", "Middleweight", 150, 100);
    }

    private ResponseDTO getDroneSearchResponseDTO_success(String serialNumber) throws Exception{
        ResponseDTO responseDTO =
                new ResponseDTO(LocalDateTime.now(), HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),  AppConstants.DRONE_INFO, getDroneDTO(serialNumber));
        return responseDTO;
    }

    private DroneDTO getDroneDTO(String serialNumber) throws Exception {
        return new DroneDTO(serialNumber, "Heavyweight", 400, 100, DroneState.LOADING.name());
    }

    private ResponseDTO getMedicationResponseDTO_success() throws Exception {
        ResponseDTO responseDTO =
                new ResponseDTO(LocalDateTime.now(), HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),
                        AppConstants.LOAD_MEDICATION_SUCCESS, getMedicationRequestDTO());
        return responseDTO;
    }

    private MedicationRequestDTO getMedicationRequestDTO() throws Exception {
        return new MedicationRequestDTO("MED-0001", 350, "CD_45445", TestConstants.getImage("PNG"), "S0001");
    }

    private ResponseDTO getDroneStatusChangeResponseDTO_success() throws Exception {
        DroneStatusChangeRequestDTO requestDTO = getDroneStatusChangeRequestDTO();
        ResponseDTO responseDTO =
                new ResponseDTO(LocalDateTime.now(), HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),
                        AppConstants.DRONE_STATUS_CHANGE_SUCCESS + requestDTO.getSerialNumber(), requestDTO);
        return responseDTO;
    }

    private DroneStatusChangeRequestDTO getDroneStatusChangeRequestDTO() throws Exception {
        return new DroneStatusChangeRequestDTO("S0001", "DELIVERING");
    }

    private List<LoadedMedicationItemDTO> getLoadedMedicationItems() throws Exception {
        return List.of(
                new LoadedMedicationItemDTO("2001", "UT5510-1420", "BT-4850", 350, TestConstants.getImage("PNG")),
                new LoadedMedicationItemDTO("2001", "UT5510-1421", "BT-4851", 250, TestConstants.getImage("PNG")),
                new LoadedMedicationItemDTO("2001", "UT5510-1422", "BT-4852", 150, TestConstants.getImage("PNG")),
                new LoadedMedicationItemDTO("2001", "UT5510-1423", "BT-4853", 200, TestConstants.getImage("PNG")),
                new LoadedMedicationItemDTO("2001", "UT5510-1424", "BT-4854", 300, TestConstants.getImage("PNG"))
        );
    }

    static String getStringObject(final Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
