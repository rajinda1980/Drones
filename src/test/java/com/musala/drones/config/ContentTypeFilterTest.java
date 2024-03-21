package com.musala.drones.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.util.AppConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Junit test class to test filter
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ContentTypeFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test Content-Type filter - happy path")
    public void testContentTypeFileter_success() throws Exception {
        DroneRequestDTO requestDTO = new DroneRequestDTO("S1000001", "Middleweight", 5000, 100);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/v1/api/drone/register")
                        .content(getStringObject(requestDTO))
                        .contentType(AppConstants.CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail[0].message").value(AppConstants.DRONE_WEIGHT_EXCEEDED));
    }

    @Test
    @DisplayName("Test Content-Type filter - Fail path")
    public void testContentTypeFilter_fail() throws Exception {
        DroneRequestDTO requestDTO = new DroneRequestDTO("S1000001", "Middleweight", 500, 100);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/api/drone/register")
                                .content(getStringObject(requestDTO))
                                .contentType("application/xml"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Content-Type. Content-Type must be application/json"));
    }

    static String getStringObject(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
