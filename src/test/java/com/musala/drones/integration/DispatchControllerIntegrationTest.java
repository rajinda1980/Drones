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

    private static WireMockServer wireMockServer;
    private String restDroneRegistrationUrl, serverDroneRegistrationUrl;
    private TestRestTemplate restTemplate;

    @MockBean
    DroneServiceImpl registrationService;

    @BeforeAll
    static void startUp(){
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void destroy() {
        wireMockServer.shutdown();
    }

    @BeforeEach
    void inti() {
        wireMockServer.resetAll();
        restTemplate = new TestRestTemplate();
        serverDroneRegistrationUrl = TestConstants.REGISTER_DRONE_URL;
        restDroneRegistrationUrl = TestConstants.LOCALHOST + wireMockServer.port() + TestConstants.REGISTER_DRONE_URL;
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
    }

    @Test
    @DisplayName("Test the integration for registering a drone - Happy path")
    void testRegisterDroneInt_success() throws Exception {
        setStub();

        String request = IOUtils.resourceToString(TestConstants.DRONE_REGISTRATION_REQUEST_JSON_SUCCESS, Charset.forName(TestConstants.CHARSET_FOR_FILE_TRANSFORM));

        ResponseEntity response = restTemplate.exchange(restDroneRegistrationUrl, HttpMethod.POST, setHeaders(request), String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

        JsonNode json = new ObjectMapper().readTree(response.getBody().toString());
        Assertions.assertEquals(Integer.parseInt(json.get("statusCode").toString()), HttpStatus.OK.value());
        Assertions.assertEquals(json.get("statusValue").toString(), "\"" + HttpStatus.OK.getReasonPhrase() + "\"");
        Assertions.assertEquals(json.get("message").toString(), "\"" + AppConstants.DRONE_REGISTERED + "\"");
        Assertions.assertEquals(json.get("object").get("serialNumber").toString(), "\"1005\"");
        Assertions.assertEquals(json.get("object").get("model").toString(), "\"Heavyweight\"");
        Assertions.assertEquals(json.get("object").get("weight").toString(), "\"450\"");
        Assertions.assertEquals(json.get("object").get("capacity").toString(), "\"100\"");
    }

    @Test
    @DisplayName("Test the integration for registering a drone - drone is registered")
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
    @DisplayName("Test integration for exceptions")
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
    @DisplayName("Test integration for multiple exceptions")
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
    @DisplayName("Test integration for invaid drone model")
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

    private HttpEntity setHeaders(String request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(request, headers);
        return entity;
    }
}
