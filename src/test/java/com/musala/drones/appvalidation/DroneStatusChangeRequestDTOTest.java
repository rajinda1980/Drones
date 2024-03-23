package com.musala.drones.appvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.dto.DroneState;
import com.musala.drones.dto.DroneStatusChangeRequestDTO;
import com.musala.drones.util.AppConstants;
import com.musala.drones.utils.TestConstants;
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
 * Junit test class to test DroneStatusChangeRequestDTO
 *
 * @author Rajinda
 * @version 1.0
 * @since 21/03/2024
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DroneStatusChangeRequestDTOTest {

    @Autowired
    MockMvc mockMvc;

    /**
     * value map
     *
     * @return value map
     * @throws Exception
     */
    static Stream<Arguments> DroneStatusChangeRequestDTO_Exceptions() throws Exception {
        DroneStatusChangeRequestDTO request_snLow = new DroneStatusChangeRequestDTO("", DroneState.IDLE.name());
        String expected_snLow = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED;

        DroneStatusChangeRequestDTO request_snHigh =
                new DroneStatusChangeRequestDTO("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" +
                        "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", DroneState.IDLE.name());
        String expected_snHigh = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED;

        DroneStatusChangeRequestDTO request_status = new DroneStatusChangeRequestDTO("S0001", null);
        String expected_status = AppConstants.DRONE_STATUS_MANDATORY;

        return Stream.of(
                Arguments.of(request_snLow, expected_snLow),
                Arguments.of(request_snHigh, expected_snHigh),
                Arguments.of(request_status, expected_status)
        );
    }

    @ParameterizedTest
    @DisplayName("Unit test for Drone Request Status change validations")
    @MethodSource
    void DroneStatusChangeRequestDTO_Exceptions(DroneStatusChangeRequestDTO statusChangeRequestDTO, String expect) throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .put(TestConstants.DRONE_STATUS_CHANGE_URL)
                        .content(getStringObject(statusChangeRequestDTO))
                        .contentType(AppConstants.CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail[0].message").value(expect)
        );
    }

    static String getStringObject(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
