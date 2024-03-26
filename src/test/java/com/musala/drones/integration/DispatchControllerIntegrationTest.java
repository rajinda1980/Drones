package com.musala.drones.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.musala.drones.service.DroneServiceImpl;
import com.musala.drones.util.AppConstants;
import com.musala.drones.utils.TestConstants;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Integration test class to test controller
 *
 * @author Rajinda
 * @version 1.0
 * @since 19/03/2024
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DispatchControllerIntegrationTest {

    private static WireMockServer wireMockServer, wireMockServer_2;
    private String restDroneRegistrationUrl, serverDroneRegistrationUrl,
            serverGetDroneUrl, restGetDroneUrl, serverGetNonExistingDroneUrl, restGetNonExistingDroneUrl,
            serverLoadMedicationUrl, restLoadMedicationUrl, serverDroneStatusChangeUrl, restDroneStatusChangeUrl,
            serverLoadedMedicationsUrl, restLoadedMedicationsUrl, restLoadedMedicationsUrl2, serverFindIdleDronesUrl,
            restFindIdleDronesUrl, restFindIdleDronesUrl2, serverGetBatteryLevelUrl, restGetBatteryLevelUrl, restGetBatteryLevelUrl2;
    private TestRestTemplate restTemplate;

    @MockBean
    DroneServiceImpl registrationService;

    @BeforeAll
    static void startUp(){
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        wireMockServer_2 = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer_2.start();
    }

    @AfterAll
    static void destroy() {
        wireMockServer.shutdown();
        wireMockServer_2.shutdown();
    }

    @BeforeEach
    void inti() {
        wireMockServer.resetAll();
        wireMockServer_2.resetAll();
        restTemplate = new TestRestTemplate();
        serverDroneRegistrationUrl = TestConstants.REGISTER_DRONE_URL;
        restDroneRegistrationUrl = TestConstants.LOCALHOST + wireMockServer.port() + TestConstants.REGISTER_DRONE_URL;
        serverGetDroneUrl = TestConstants.DRONE_GET_URL;
        restGetDroneUrl = TestConstants.LOCALHOST + wireMockServer.port() + TestConstants.DRONE_GET_URL;
        serverGetNonExistingDroneUrl = TestConstants.DRONE_GET_URL;
        restGetNonExistingDroneUrl = TestConstants.LOCALHOST + wireMockServer_2.port() + TestConstants.DRONE_GET_URL;
        serverLoadMedicationUrl = TestConstants.LOAD_MEDICATION_URL;
        restLoadMedicationUrl = TestConstants.LOCALHOST + wireMockServer.port() + TestConstants.LOAD_MEDICATION_URL;
        serverDroneStatusChangeUrl = TestConstants.DRONE_STATUS_CHANGE_URL;
        restDroneStatusChangeUrl = TestConstants.LOCALHOST + wireMockServer.port() + TestConstants.DRONE_STATUS_CHANGE_URL;
        serverLoadedMedicationsUrl = TestConstants.LOADED_MEDICATION_ITEM_URL;
        restLoadedMedicationsUrl = TestConstants.LOCALHOST + wireMockServer.port() + TestConstants.LOADED_MEDICATION_ITEM_URL;
        restLoadedMedicationsUrl2 = TestConstants.LOCALHOST + wireMockServer_2.port() + TestConstants.LOADED_MEDICATION_ITEM_URL;
        serverFindIdleDronesUrl = TestConstants.FIND_IDLE_DRONES;
        restFindIdleDronesUrl = TestConstants.LOCALHOST + wireMockServer.port() + TestConstants.FIND_IDLE_DRONES;
        restFindIdleDronesUrl2 = TestConstants.LOCALHOST + wireMockServer_2.port() + TestConstants.FIND_IDLE_DRONES;
        serverGetBatteryLevelUrl = TestConstants.GET_BATTERY_LEVEL;
        restGetBatteryLevelUrl = TestConstants.LOCALHOST + wireMockServer.port() + TestConstants.GET_BATTERY_LEVEL;
        restGetBatteryLevelUrl2 = TestConstants.LOCALHOST + wireMockServer_2.port() + TestConstants.GET_BATTERY_LEVEL;
    }

    /**
     * Mock server responses
     *
     * @throws Exception
     */
    private void setStub() throws Exception {
        // Register drone - success path
        String registerDroneRequest = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String registerDroneResponse = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(registerDroneRequest))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(registerDroneResponse)
                        )
        );

        // Register drone - Drone is registered
        String droneRequest_registered = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_ALREADY_REGISTERED, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String droneResponse_registered = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_ALREADY_REGISTERED, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(droneRequest_registered))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(droneResponse_registered)
                        )
        );

        // Register drone - Invalid serial number
        String droneRequest_invalid_serialnumber = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String droneResponse_invalid_serialnumber = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(droneRequest_invalid_serialnumber))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(droneResponse_invalid_serialnumber)
                        )
        );

        // Register drone - Invalid model
        String droneRequest_invalid_model = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_INVALID_MODEL, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String droneResponse_invalid_model = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_INVALID_MODEL, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(droneRequest_invalid_model))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(droneResponse_invalid_model)
                        )
        );

        // Register drone - weight low
        String droneRequest_weight_low = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_WEIGHT_LOW, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String droneResponse_weight_low = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_WEIGHT_LOW, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(droneRequest_weight_low))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(droneResponse_weight_low)
                        )
        );

        // Register drone - weight high
        String droneRequest_weight_high = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_WEIGHT_HIGH, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String droneResponse_weight_high = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_WEIGHT_HIGH, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(droneRequest_weight_high))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(droneResponse_weight_high)
                        )
        );

        // Register drone - capacity low
        String droneRequest_capacity_low = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_CAPACITY_LOW, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String droneResponse_capacity_low = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_CAPACITY_LOW, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(droneRequest_capacity_low))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(droneResponse_capacity_low)
                        )
        );

        // Register drone - capacity high
        String droneRequest_capacity_high = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_CAPACITY_HIGH, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String droneResponse_capacity_high = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_CAPACITY_HIGH, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(droneRequest_capacity_high))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(droneResponse_capacity_high)
                        )
        );

        // Register drone - multiple exceptions
        String droneRequest_weight_capacity = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_INVALID_WEIGHT_CAPACITY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String droneResponse_weight_capacity = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_RESPONSE_JSON_INVALID_WEIGHT_CAPACITY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneRegistrationUrl)
                        .withRequestBody(equalToJson(droneRequest_weight_capacity))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(droneResponse_weight_capacity)
                        )
        );

        // Get drone - success result
        String getDrone_success = IOUtils.resourceToString(TestConstants.GET_DRONE_RESPONSE_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.get(serverGetDroneUrl + 1001)
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(getDrone_success)
                        )
        );

        /* ********************************* LOAD MEDICATION ********************************* */

        String loadMedicationRequest_success = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_success = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_success))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_success)
                        )
        );

        String loadMedicationRequest_codeMandatory = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_CODE_MANDATORY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_codeMandatory = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_CODE_MANDATORY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_codeMandatory))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_codeMandatory)
                        )
        );

        String loadMedicationRequest_notOccupied = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_DRONE_NOT_OCCUPIED, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_notOccupied = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_DRONE_NOT_OCCUPIED, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_notOccupied))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_notOccupied)
                        )
        );

        String loadMedicationRequest_exceedWeight = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_EXCEED_WEIGHT, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_exceedWeight = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_EXCEED_WEIGHT, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_exceedWeight))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_exceedWeight)
                        )
        );

        String loadMedicationRequest_invalidCode = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_INVALID_CODE, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_invalidCode = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_INVALID_CODE, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_invalidCode))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_invalidCode)
                        )
        );

        String loadMedicationRequest_invalidName = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_INVALID_NAME, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_invalidName = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_INVALID_NAME, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_invalidName))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_invalidName)
                        )
        );

        String loadMedicationRequest_invalidSerialNumber = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_invalidSerialNumber = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_invalidSerialNumber))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_invalidSerialNumber)
                        )
        );

        String loadMedicationRequest_lowCapacity = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_LOW_CAPACITY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_lowCapacity = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_LOW_CAPACITY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_lowCapacity))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_lowCapacity)
                        )
        );

        String loadMedicationRequest_weightMandatory = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_WEIGHT_MANDATORY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_weightMandatory = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_WEIGHT_MANDATORY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_weightMandatory))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_weightMandatory)
                        )
        );

        String loadMedicationRequest_withoutName = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_WITHOUT_NAME, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String loadMedicationResponse_withoutName = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_RESPONSE_JSON_WITHOUT_NAME, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverLoadMedicationUrl)
                        .withRequestBody(equalToJson(loadMedicationRequest_withoutName))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadMedicationResponse_withoutName)
                        )
        );

        /* ********************************* STATUS CHANGE ********************************* */

        String statusChangeRequest_success = IOUtils.resourceToString(TestConstants.DRONE_STATUS_CHANGE_REQUEST_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String statusChangeResponse_success = IOUtils.resourceToString(TestConstants.DRONE_STATUS_CHANGE_RESPONSE_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneStatusChangeUrl)
                        .withRequestBody(equalToJson(statusChangeRequest_success))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(statusChangeResponse_success)
                        )
        );

        String statusChangeRequest_invalidSN = IOUtils.resourceToString(TestConstants.DRONE_STATUS_CHANGE_REQUEST_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        String statusChangeResponse_invalidSN = IOUtils.resourceToString(TestConstants.DRONE_STATUS_CHANGE_RESPONSE_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.post(serverDroneStatusChangeUrl)
                        .withRequestBody(equalToJson(statusChangeRequest_invalidSN))
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(statusChangeResponse_invalidSN)
                        )
        );

        /* ********************************* FIND LOADED MEDICATION ********************************* */

        String loadedMedicationResponse_success = IOUtils.resourceToString(TestConstants.LOADED_MEDICATION_RESPONSE_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.get(serverLoadedMedicationsUrl + 1001)
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadedMedicationResponse_success)
                        )
        );

        /* ********************************* FIND IDLE DRONES ********************************* */

        String findIdleDronesResponse_success = IOUtils.resourceToString(TestConstants.FIND_IDLE_DRONES_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer.stubFor(
                WireMock.get(serverFindIdleDronesUrl)
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(findIdleDronesResponse_success)
                        )
        );

        /* ********************************* DRONE BATTERY LEVEL ********************************* */

        wireMockServer.stubFor(
                WireMock.get(serverGetBatteryLevelUrl + 3005)
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody("100")
                        )
        );

    }

    /**
     * Mock server 2 response
     *
     * @throws Exception
     */
    private void setStub2() throws Exception {
        // Get drone - drone does not exist
        String getDrone_droneNotExist = IOUtils.resourceToString(TestConstants.GET_DRONE_RESPONSE_JSON_NOT_EXIST, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer_2.stubFor(
                WireMock.get(serverGetNonExistingDroneUrl + 1005)
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(getDrone_droneNotExist)
                        )
        );

        /* ********************************* FIND LOADED MEDICATION ********************************* */

        String loadedMedicationResponse_noitem = IOUtils.resourceToString(TestConstants.LOADED_MEDICATION_RESPONSE_JSON_NO_MEDICATIONS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer_2.stubFor(
                WireMock.get(serverLoadedMedicationsUrl + 1002)
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(loadedMedicationResponse_noitem)
                        )
        );

        /* ********************************* FIND IDLE DRONES ********************************* */

        String findIdleDronesResponse_noItem = IOUtils.resourceToString(TestConstants.FIND_IDLE_DRONES_JSON_NO_RECORDS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer_2.stubFor(
                WireMock.get(serverFindIdleDronesUrl)
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(findIdleDronesResponse_noItem)
                        )
        );

        /* ********************************* DRONE BATTERY LEVEL ********************************* */

        String getBatteryLevel_invalidSN = IOUtils.resourceToString(TestConstants.GET_BATTERY_LEVEL_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        wireMockServer_2.stubFor(
                WireMock.get(serverGetBatteryLevelUrl)
                        .withHeader(HttpHeaders.CONTENT_TYPE, containing(AppConstants.CONTENT_TYPE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.BAD_REQUEST.value())
                                        .withHeader(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE)
                                        .withBody(getBatteryLevel_invalidSN)
                        )
        );
    }

    @Test
    @DisplayName("Integration => Integration => Test registering a drone - Happy path")
    void testRegisterDroneInt_success() throws Exception {
        setStub();

        String request = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));

        ResponseEntity response = restTemplate.exchange(restDroneRegistrationUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK.value(), Integer.parseInt(json.get("statusCode").toString()));
        Assertions.assertEquals("\"" + HttpStatus.OK.getReasonPhrase() + "\"", json.get("statusValue").toString());
        Assertions.assertEquals("\"" + AppConstants.DRONE_REGISTERED + "\"", json.get("message").toString());
        Assertions.assertEquals("\"1005\"", json.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"Heavyweight\"", json.get("object").get("model").toString());
        Assertions.assertEquals("\"450\"", json.get("object").get("weight").toString());
        Assertions.assertEquals("\"100\"", json.get("object").get("capacity").toString());
    }

    @Test
    @DisplayName("Integration => Test registering a drone - exception if the drone is registered")
    void testRegisterDroneInt_alreadyRegistered() throws Exception {
        setStub();

        String request = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_ALREADY_REGISTERED, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));

        ResponseEntity response = restTemplate.exchange(restDroneRegistrationUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.REGISTER_DRONE_URL + "\"", json.get("path").toString());
        Assertions.assertEquals("\"" + AppConstants.DRONE_REGISTERED_EXCEPTION + "\"", json.get("detail").get(0).get("message").toString());
    }

    /**
     * Value map for testing exceptions
     *
     * @return arguments
     * @throws Exception
     */
    static Stream testRegisterDroneInt_exceptions() throws Exception {
        String invalidSN = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailSN = new ConcurrentHashMap<>();
        detailSN.put("message", "\"" + AppConstants.SERIAL_NUMBER_LENGTH_EXCEED + "\"");
        detailSN.put("fieldName", "\"serialNumber\"" );
        detailSN.put("fieldValue", "\"\"" );

        String weightLow = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_WEIGHT_LOW, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailWLow = new ConcurrentHashMap<>();
        detailWLow.put("message", "\"" + AppConstants.DRONE_WEIGHT_LOW + "\"");
        detailWLow.put("fieldName", "\"weight\"" );
        detailWLow.put("fieldValue", "0" );

        String weightHigh = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_WEIGHT_HIGH, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailWHigh = new ConcurrentHashMap<>();
        detailWHigh.put("message", "\"" + AppConstants.DRONE_WEIGHT_EXCEEDED + "\"");
        detailWHigh.put("fieldName", "\"weight\"" );
        detailWHigh.put("fieldValue", "850" );

        String capacityLow = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_CAPACITY_LOW, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailCLow = new ConcurrentHashMap<>();
        detailCLow.put("message", "\"" + AppConstants.BATTERY_CAPACITY_LOW + "\"");
        detailCLow.put("fieldName", "\"capacity\"" );
        detailCLow.put("fieldValue", "0" );

        String capacityHigh = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_CAPACITY_HIGH, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailCHigh = new ConcurrentHashMap<>();
        detailCHigh.put("message", "\"" + AppConstants.BATTERY_CAPACITY_EXCEEDED + "\"");
        detailCHigh.put("fieldName", "\"capacity\"" );
        detailCHigh.put("fieldValue", "200" );

        return Stream.of(
                Arguments.of(invalidSN, detailSN),
                Arguments.of(weightLow, detailWLow),
                Arguments.of(weightHigh, detailWHigh),
                Arguments.of(capacityLow, detailCLow),
                Arguments.of(capacityHigh, detailCHigh)
        );
    }

    @ParameterizedTest
    @DisplayName("Integration => Test register drone - exceptions")
    @MethodSource
    void testRegisterDroneInt_exceptions(String request, Map<String, String> detail) throws Exception {
        setStub();

        ResponseEntity response = restTemplate.exchange(restDroneRegistrationUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.REGISTER_DRONE_URL + "\"", json.get("path").toString());
        Assertions.assertEquals(detail.get("message"), json.get("detail").get(0).get("message").toString());
        Assertions.assertEquals(detail.get("fieldName"), json.get("detail").get(0).get("fieldName").toString());
        Assertions.assertEquals(detail.get("fieldValue"), json.get("detail").get(0).get("fieldValue").toString());
    }

    @Test
    @DisplayName("Integration => Test register drone - multiple exceptions")
    void testRegisterDroneInt_multipleExceptions() throws Exception {
        setStub();

        String request = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_INVALID_WEIGHT_CAPACITY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));

        ResponseEntity response = restTemplate.exchange(restDroneRegistrationUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.REGISTER_DRONE_URL + "\"", json.get("path").toString());

        Assertions.assertEquals("\"" + AppConstants.SERIAL_NUMBER_LENGTH_EXCEED + "\"", json.get("detail").get(0).get("message").toString());
        Assertions.assertEquals("\"serialNumber\"", json.get("detail").get(0).get("fieldName").toString());
        Assertions.assertEquals("\"\"", json.get("detail").get(0).get("fieldValue").toString());

        Assertions.assertEquals("\"" + AppConstants.BATTERY_CAPACITY_EXCEEDED + "\"", json.get("detail").get(2).get("message").toString());
        Assertions.assertEquals("\"capacity\"", json.get("detail").get(2).get("fieldName").toString());
        Assertions.assertEquals("200", json.get("detail").get(2).get("fieldValue").toString());

        Assertions.assertEquals("\"" + AppConstants.DRONE_WEIGHT_EXCEEDED + "\"", json.get("detail").get(1).get("message").toString());
        Assertions.assertEquals("\"weight\"", json.get("detail").get(1).get("fieldName").toString());
        Assertions.assertEquals("800", json.get("detail").get(1).get("fieldValue").toString());
    }

    @Test
    @DisplayName("Integration => Test register drone - exception if drone model is invalid")
    void testRegisterDroneInt_InvalidModel() throws Exception {
        setStub();

        String request = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_INVALID_MODEL, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));

        ResponseEntity response = restTemplate.exchange(restDroneRegistrationUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.REGISTER_DRONE_URL + "\"", json.get("path").toString());
        Assertions.assertEquals("\"" + TestConstants.INVALID_DRONE_MODEL + "\"", json.get("detail").get(0).get("message").toString());
    }

    @Test
    @DisplayName("Integration => Test get drone details - happy path")
    void testGetDrone_success() throws Exception {
        setStub();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(restGetDroneUrl + 1001, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("createdDate").toString());
        Assertions.assertEquals(HttpStatus.OK.value(), Integer.parseInt(json.get("statusCode").toString()));
        Assertions.assertEquals("\"" + HttpStatus.OK.getReasonPhrase() + "\"", json.get("statusValue").toString());
        Assertions.assertEquals("\"" + AppConstants.DRONE_INFO + "\"", json.get("message").toString());
        Assertions.assertEquals("\"1001\"", json.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"Heavyweight\"", json.get("object").get("model").toString());
        Assertions.assertEquals("\"500\"", json.get("object").get("weight").toString());
        Assertions.assertEquals("\"90\"", json.get("object").get("capacity").toString());
        Assertions.assertEquals("\"IDLE\"", json.get("object").get("status").toString());
    }

    @Test
    @DisplayName("Integration => Test get drone details - exception if drone does not exist ")
    void testGetDrone_droneDoesNotExist() throws Exception {
        setStub2();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(restGetNonExistingDroneUrl + 1005, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + AppConstants.DRONE_DOES_NOT_EXIST + 1005 + "\"", json.get("detail").get(0).get("message").toString());
    }

    @Test
    @DisplayName("Integration => Test load medication - Happy path")
    void testLoadMedication_success() throws Exception {
        setStub();

        String request = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));

        ResponseEntity response = restTemplate.exchange(restLoadMedicationUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK.value(), Integer.parseInt(json.get("statusCode").toString()));
        Assertions.assertEquals("\"" + HttpStatus.OK.getReasonPhrase() + "\"", json.get("statusValue").toString());
        Assertions.assertEquals("\"" + AppConstants.LOAD_MEDICATION_SUCCESS + "\"", json.get("message").toString());
        Assertions.assertEquals("\"1001\"", json.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"MI0001\"", json.get("object").get("name").toString());
        Assertions.assertEquals("\"500\"", json.get("object").get("weight").toString());
        Assertions.assertEquals("\"AXP_112\"", json.get("object").get("code").toString());
    }

    /**
     * Value map
     *
     * @return Argument list
     * @throws Exception
     */
    static Stream<Arguments> testLoadMedication_exceptions() throws Exception {
        String codeMandatory = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_CODE_MANDATORY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailCodeMandatory = new ConcurrentHashMap<>();
        detailCodeMandatory.put("message", "\"" + AppConstants.INVALID_MEDICATION_CODE_LENGTH + "\"");
        detailCodeMandatory.put("fieldName", "\"code\"" );
        detailCodeMandatory.put("fieldValue", "\"\"" );

        String notOccupied = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_DRONE_NOT_OCCUPIED, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailNO = new ConcurrentHashMap<>();
        detailNO.put("message", "\"" + AppConstants.DRONE_NOT_OCCUPIED + "\"");

        String exceedWeight = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_EXCEED_WEIGHT, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailExceedWeight = new ConcurrentHashMap<>();
        detailExceedWeight.put("message", "\"" + AppConstants.DRONE_OVERWEIGHT + "450 grams\"");

        String invalidCode = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_INVALID_CODE, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailIC = new ConcurrentHashMap<>();
        detailIC.put("message", "\"" + AppConstants.INVALID_MEDICATION_CODE_TEXT + "\"");
        detailIC.put("fieldName", "\"code\"" );
        detailIC.put("fieldValue", "\"AXP_sn_112\"" );

        String invalidName = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_INVALID_NAME, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailName = new ConcurrentHashMap<>();
        detailName.put("message", "\"" + AppConstants.INVALID_MEDICATION_NAME + "\"");
        detailName.put("fieldName", "\"name\"" );
        detailName.put("fieldValue", "\"A#125_2345\"" );

        String invalidSN = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailSN= new ConcurrentHashMap<>();
        detailSN.put("message", "\"" + AppConstants.DRONE_DOES_NOT_EXIST + "1002\"");

        String lowCap = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_LOW_CAPACITY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailLCap = new ConcurrentHashMap<>();
        detailLCap.put("message", "\"" + AppConstants.LOW_CAPACITY + "\"");

        String weightMan = IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_WEIGHT_MANDATORY, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailWMan = new ConcurrentHashMap<>();
        detailWMan.put("message", "\"" + AppConstants.EMPTY_MEDICATION_WEIGHT + "\"");
        detailWMan.put("fieldName", "\"weight\"" );
        detailWMan.put("fieldValue", "null" );

        String withoutName= IOUtils.resourceToString(TestConstants.LOAD_MEDICATION_REQUEST_JSON_WITHOUT_NAME, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));
        Map<String, String> detailWName = new ConcurrentHashMap<>();
        detailWName.put("message", "\"" + AppConstants.MEDICATION_NAME_LENGTH_EXCEEDED + "\"");
        detailWName.put("fieldName", "\"name\"" );
        detailWName.put("fieldValue", "\"\"" );

        return Stream.of(
                Arguments.of(codeMandatory, detailCodeMandatory),
                Arguments.of(notOccupied, detailNO),
                Arguments.of(exceedWeight, detailExceedWeight),
                Arguments.of(invalidCode, detailIC),
                Arguments.of(invalidName, detailName),
                Arguments.of(invalidSN, detailSN),
                Arguments.of(lowCap, detailLCap),
                Arguments.of(weightMan, detailWMan),
                Arguments.of(withoutName, detailWName)
        );
    }

    @ParameterizedTest
    @DisplayName("Integration => Test load medication - exceptions")
    @MethodSource
    void testLoadMedication_exceptions(String request, Map<String, String> detail) throws Exception {
        setStub();

        ResponseEntity response = restTemplate.exchange(restLoadMedicationUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.LOAD_MEDICATION_URL + "\"", json.get("path").toString());
        Assertions.assertEquals(detail.get("message"), json.get("detail").get(0).get("message").toString());
        if (detail.containsKey("fieldName") && detail.containsKey("fieldValue")) {
            Assertions.assertEquals(detail.get("fieldName"), json.get("detail").get(0).get("fieldName").toString());
            Assertions.assertEquals(detail.get("fieldValue"), json.get("detail").get(0).get("fieldValue").toString());
        }
    }

    @Test
    @DisplayName("Integration => Test drone status change - happy path")
    void testStatusChange_success() throws Exception {
        setStub();

        String request = IOUtils.resourceToString(TestConstants.DRONE_STATUS_CHANGE_REQUEST_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));

        ResponseEntity response = restTemplate.exchange(restDroneStatusChangeUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK.value(), Integer.parseInt(json.get("statusCode").toString()));
        Assertions.assertEquals("\"" + HttpStatus.OK.getReasonPhrase() + "\"", json.get("statusValue").toString() );
        Assertions.assertEquals("\"" + AppConstants.DRONE_STATUS_CHANGE_SUCCESS + "1003\"", json.get("message").toString());
        Assertions.assertEquals("\"1003\"", json.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"LOADED\"", json.get("object").get("status").toString());
    }

    @Test
    @DisplayName("Integration => Test drone status change - Invalid serial number")
    void testStatusChange_invalidSerialnumber() throws Exception {
        setStub();

        String request = IOUtils.resourceToString(TestConstants.DRONE_STATUS_CHANGE_REQUEST_JSON_INVALID_SERIALNUMBER, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));

        ResponseEntity response = restTemplate.exchange(restDroneStatusChangeUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.DRONE_STATUS_CHANGE_URL+ "\"", json.get("path").toString());
        Assertions.assertEquals("\"" + AppConstants.DRONE_DOES_NOT_EXIST + "1004\"", json.get("detail").get(0).get("message").toString());
    }

    @Test
    @DisplayName("Integration => Test case for verifying loaded medication items - happy path")
    void testFindLoadedMedicationItems_success() throws Exception {
        setStub();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(restLoadedMedicationsUrl + 1001, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertEquals("\"1001\"", json.get(0).get("serialNumber").toString());
        Assertions.assertEquals("\"A125_2345\"", json.get(0).get("name").toString());
        Assertions.assertEquals("\"AXP_112\"", json.get(0).get("code").toString());
        Assertions.assertEquals("\"400\"", json.get(0).get("weight").toString());

        Assertions.assertEquals("\"1001\"", json.get(1).get("serialNumber").toString());
        Assertions.assertEquals("\"BT6500_001\"", json.get(1).get("name").toString());
        Assertions.assertEquals("\"BT_145\"", json.get(1).get("code").toString());
        Assertions.assertEquals("\"400\"", json.get(1).get("weight").toString());

        Assertions.assertEquals("\"1001\"", json.get(2).get("serialNumber").toString());
        Assertions.assertEquals("\"BT6500_005\"", json.get(2).get("name").toString());
        Assertions.assertEquals("\"BT_170\"", json.get(2).get("code").toString());
        Assertions.assertEquals("\"200\"", json.get(2).get("weight").toString());
    }

    @Test
    @DisplayName("Integration => Test case for verifying loaded medication items - exception")
    void testFindLoadedMedicationItems_exception() throws Exception {
        setStub2();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(restLoadedMedicationsUrl2 + 1002, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.LOADED_MEDICATION_ITEM_URL + "1002\"", json.get("path").toString());
        Assertions.assertEquals("\"" + AppConstants.NO_MEDICATION_ITEMS_FOUND + "1002\"", json.get("detail").get(0).get("message").toString());
    }

    @Test
    @DisplayName("Integration => Test case for verifying finding idle drones - happy path")
    void testFindIdleDrones_success() throws Exception {
        setStub();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(restFindIdleDronesUrl, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertEquals("\"1001\"", json.get(0).get("serialNumber").toString());
        Assertions.assertEquals("\"Lightweight\"", json.get(0).get("model").toString());
        Assertions.assertEquals("\"200\"", json.get(0).get("weight").toString());
        Assertions.assertEquals("\"100\"", json.get(0).get("capacity").toString());

        Assertions.assertEquals("\"1002\"", json.get(1).get("serialNumber").toString());
        Assertions.assertEquals("\"Lightweight\"", json.get(1).get("model").toString());
        Assertions.assertEquals("\"200\"", json.get(1).get("weight").toString());
        Assertions.assertEquals("\"100\"", json.get(1).get("capacity").toString());
    }

    @Test
    @DisplayName("Integration => Test case for verifying finding idle drones - exception")
    void testFindIdleDrones_exception() throws Exception {
        setStub2();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(restFindIdleDronesUrl2, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.FIND_IDLE_DRONES + "\"", json.get("path").toString());
        Assertions.assertEquals("\"" + AppConstants.NO_AVAILABLE_DRONE_FOUND + "\"", json.get("detail").get(0).get("message").toString());
    }

    @Test
    @DisplayName("Integration => Test case for verifying get battery level - happy path")
    void testGetDroneBatteryLevel_success() throws Exception {
        setStub();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(restGetBatteryLevelUrl + 3005, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertEquals("100", json.toString());
    }

    @Test
    @DisplayName("Integration => Test case for verifying get battery level - exception")
    void testGetDroneBatteryLevel_exception() throws Exception {
        setStub2();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(restGetBatteryLevelUrl2, HttpMethod.GET, entity, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertNotNull(json.get("timestamp").toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), Integer.parseInt(json.get("status").toString()));
        Assertions.assertEquals("\"" + TestConstants.GET_BATTERY_LEVEL + "1003\"", json.get("path").toString());
        Assertions.assertEquals("\"" + AppConstants.DRONE_DOES_NOT_EXIST + "1003\"", json.get("detail").get(0).get("message").toString());
    }

    private HttpEntity setHeaders(String request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(request, headers);
        return entity;
    }
}
