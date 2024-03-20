package com.musala.drones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.ErrorDetailDTO;
import com.musala.drones.dto.ErrorResponseDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.service.RegistrationServiceImpl;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    RegistrationServiceImpl registrationService;

    @Autowired
    MockMvc mockMvc;

    private String droneRegistrationURL;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        droneRegistrationURL = "/v1/api/drone/register";
    }

    @Test
    @DisplayName("Test the functionality for registering a drone - Happy path")
    void testRegisterDrone_success() throws Exception {
        DroneRequestDTO requestDTO = getRequestDTO();
        ResponseDTO responseDTO = getResponseDTO_success(requestDTO);

        Mockito.when(registrationService.registerDrone(isA(DroneRequestDTO.class))).thenReturn(responseDTO);
        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(droneRegistrationURL)
                                .contentType(AppConstants.CONTENT_TYPE)
                                .content(getStringObject(getRequestDTO()))
                ).andReturn();

        Gson gson = TestConstants.getFullyFledgedGson();
        ResponseDTO response = gson.fromJson(result.getResponse().getContentAsString(), ResponseDTO.class);

        Assertions.assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        Assertions.assertEquals(response.getStatusValue(), HttpStatus.OK.getReasonPhrase());
        Assertions.assertEquals(response.getMessage(), AppConstants.DRONE_REGISTERED);

        Map fromResponse = (Map) response.getObject();
        Assertions.assertEquals(fromResponse.get("serialNumber"), requestDTO.getSerialNumber());
        Assertions.assertEquals(fromResponse.get("model"), requestDTO.getModel());
        Assertions.assertEquals(fromResponse.get("weight").toString(), requestDTO.getWeight().toString());
        Assertions.assertEquals(fromResponse.get("capacity").toString(), requestDTO.getCapacity().toString());
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

        DroneRequestDTO requestDTO_mode = new DroneRequestDTO("D102030", "Mediumweight", 250, 50);
        List detailsMode = new ArrayList();
        ErrorDetailDTO errorDetailMode = new ErrorDetailDTO();
        errorDetailMode.setFieldName("model");
        errorDetailMode.setFieldValue("Mediumweight");
        errorDetailMode.setMessage(AppConstants.INVALID_MODEL);
        detailsMode.add(errorDetailMode);
        ErrorResponseDTO responseDTO_SNMode = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), detailsMode, "/v1/api/drone/register");

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
                Arguments.of(requestDTO_mode, responseDTO_SNMode),
                Arguments.of(requestDTO_weight_low, responseDTO_weight_low),
                Arguments.of(requestDTO_weight_high, responseDTO_weight_high),
                Arguments.of(requestDTO_capacity_low, responseDTO_capacity_low),
                Arguments.of(requestDTO_capacity_high, responseDTO_capacity_high)
        );
    }

    @ParameterizedTest
    @DisplayName("Test the functionality for registering a drone - Exception scenario")
    @MethodSource
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

        Assertions.assertEquals(result.getResponse().getStatus(), expected.getStatus());
        Assertions.assertEquals(response.getStatus(), expected.getStatus());
        Assertions.assertTrue(response.getDetail().size() > 0);

        Map fromResponse = (Map) response.getDetail().get(0);
        ErrorDetailDTO expectedErrorDetail = (ErrorDetailDTO)  expected.getDetail().get(0);
        Assertions.assertEquals(fromResponse.get("message"), expectedErrorDetail.getMessage());
        Assertions.assertEquals(fromResponse.get("fieldName"), expectedErrorDetail.getFieldName());
        Assertions.assertEquals(fromResponse.get("fieldValue").toString(), expectedErrorDetail.getFieldValue());
    }

    private DroneRequestDTO getRequestDTO() {
        DroneRequestDTO requestDTO = new DroneRequestDTO("D001", "Middleweight", 150, 100);
        return requestDTO;
    }

    private ResponseDTO getResponseDTO_success(DroneRequestDTO requestDTO) {
        ResponseDTO responseDTO =
                new ResponseDTO(LocalDateTime.now(), HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), AppConstants.DRONE_REGISTERED, requestDTO);
        return responseDTO;
    }

    static String getStringObject(final Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
