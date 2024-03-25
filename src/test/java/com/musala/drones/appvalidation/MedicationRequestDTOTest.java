package com.musala.drones.appvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.dto.MedicationRequestDTO;
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
 * Junit test class to MedicationRequestDTO
 *
 * @author Rajinda
 * @version 1.0
 * @since 21/03/2024
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MedicationRequestDTOTest {

    @Autowired
    MockMvc mockMvc;

    /**
     * Value map
     *
     * @return Argument map
     * @throws Exception
     */
    static Stream<Arguments> testMedicationRequestDTO_Exception() throws Exception {
        MedicationRequestDTO requestDTO_ExpName = new MedicationRequestDTO("ME-001_$", 250, "MD001", TestConstants.getImage("PNG"), "S00001");
        String expected_expname = AppConstants.INVALID_MEDICATION_NAME;

        MedicationRequestDTO requestDTO_NameExceeded =
                new MedicationRequestDTO("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" +
                        "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz",
                        250, "MD001", TestConstants.getImage("PNG"), "S00001");
        String expected_length = AppConstants.MEDICATION_NAME_LENGTH_EXCEEDED;

        MedicationRequestDTO requestDTO_weight = new MedicationRequestDTO("ME-001", null, "MD001", TestConstants.getImage("PNG"), "S00001");
        String expected_weight = AppConstants.EMPTY_MEDICATION_WEIGHT;

        MedicationRequestDTO requestDTO_codeLow = new MedicationRequestDTO("ME-001", 250, "", TestConstants.getImage("PNG"), "S00001");
        String expected_codeLow = AppConstants.INVALID_MEDICATION_CODE_LENGTH;

        MedicationRequestDTO requestDTO_codeHigh =
                new MedicationRequestDTO("ME-001", 250,
                        "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
                        TestConstants.getImage("PNG"), "S00001");
        String expected_codeHigh = AppConstants.INVALID_MEDICATION_CODE_LENGTH;

        MedicationRequestDTO requestDTO_code = new MedicationRequestDTO("ME-001", 250, "aed_220", TestConstants.getImage("PNG"), "S00001");
        String expected_code = AppConstants.INVALID_MEDICATION_CODE_TEXT;

        MedicationRequestDTO requestDTO_snLow = new MedicationRequestDTO("ME-001", 250, "AED_220", TestConstants.getImage("PNG"), "");
        String expected_snLow = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED;

        MedicationRequestDTO requestDTO_snHigh =
                new MedicationRequestDTO("ME-001", 250, "AED_220", TestConstants.getImage("PNG"),
                        "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
        String expected_snHigh = AppConstants.SERIAL_NUMBER_LENGTH_EXCEED;

        MedicationRequestDTO requestDTO_image = new MedicationRequestDTO("ME-001", 250, "MD001", null, "S00001");
        String expected_image = AppConstants.EMPTY_IMAGE;

        return Stream.of(
                Arguments.of(requestDTO_ExpName, expected_expname),
                Arguments.of(requestDTO_NameExceeded, expected_length),
                Arguments.of(requestDTO_weight, expected_weight),
                Arguments.of(requestDTO_codeLow, expected_codeLow),
                Arguments.of(requestDTO_codeHigh, expected_codeHigh),
                Arguments.of(requestDTO_code, expected_code),
                Arguments.of(requestDTO_snLow, expected_snLow),
                Arguments.of(requestDTO_snHigh, expected_snHigh),
                Arguments.of(requestDTO_image, expected_image)
        );
    }

    @ParameterizedTest
    @DisplayName("Unit test for MedicationRequestDTO")
    @MethodSource
    void testMedicationRequestDTO_Exception(MedicationRequestDTO medicationRequestDTO, String expect) throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(TestConstants.LOAD_MEDICATION_URL)
                        .content(getStringObject(medicationRequestDTO))
                        .contentType(AppConstants.CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail[0].message").value(expect)
        );
    }

    static String getStringObject(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
