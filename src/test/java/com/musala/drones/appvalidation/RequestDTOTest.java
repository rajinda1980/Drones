package com.musala.drones.appvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.util.AppConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Junit test class to test DTO
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RequestDTOTest {

    @Autowired
    MockMvc mockMvc;

    /**
     * Parameter value map to test DroneRequestDTO
     *
     * @return value map
     * @throws Exception
     */
    static Stream<Arguments> testDroneRequestDTO_Exceptions() throws Exception {
        DroneRequestDTO requestDTO_SNMin = new DroneRequestDTO("", "Lightweight", 100, 50);
        String requestDTO_SNMin_response = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED;

        DroneRequestDTO requestDTO_SNMax = new DroneRequestDTO("D1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_1234567890_",
                "Middleweight", 250, 75);
        String requestDTO_SNMax_response = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED;

        DroneRequestDTO requestDTO_weight_low = new DroneRequestDTO("D102030", "Middleweight", 0, 100);
        String requestDTO_weight_low_response = AppConstants.DRONE_WEIGHT_LOW;

        DroneRequestDTO requestDTO_weight_high = new DroneRequestDTO("D102030", "Middleweight", 750, 100);
        String requestDTO_weight_high_response = AppConstants.DRONE_WEIGHT_EXCEEDED;

        DroneRequestDTO requestDTO_capacity_low = new DroneRequestDTO("D102030", "Middleweight", 250, 0);
        String requestDTO_capacity_low_response = AppConstants.BATTERY_CAPACITY_LOW;

        DroneRequestDTO requestDTO_capacity_high = new DroneRequestDTO("D102030", "Middleweight", 250, 150);
        String requestDTO_capacity_high_response = AppConstants.BATTERY_CAPACITY_EXCEEDED;

        return Stream.of(
                Arguments.of(requestDTO_SNMin, requestDTO_SNMin_response),
                Arguments.of(requestDTO_SNMax, requestDTO_SNMax_response),
                Arguments.of(requestDTO_weight_low, requestDTO_weight_low_response),
                Arguments.of(requestDTO_weight_high, requestDTO_weight_high_response),
                Arguments.of(requestDTO_capacity_low, requestDTO_capacity_low_response),
                Arguments.of(requestDTO_capacity_high, requestDTO_capacity_high_response)
        );
    }

    @ParameterizedTest
    @DisplayName("Unit test for DroneRequestDTO class")
    @MethodSource
    void testDroneRequestDTO_Exceptions(DroneRequestDTO requestDTO, String expect) throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/api/drone/register")
                                .content(getStringObject(requestDTO))
                                .contentType(AppConstants.CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail[0].message").value(expect));
    }

    static String getStringObject(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
