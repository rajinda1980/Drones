package com.musala.drones.endtoend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.controller.DispatchController;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.datamodel.repository.MedicationRepository;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.DroneStatusChangeRequestDTO;
import com.musala.drones.dto.MedicationRequestDTO;
import com.musala.drones.util.AppConstants;
import com.musala.drones.utils.TestConstants;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * End to end test cases for dispatch controller
 *
 * @author Rajinda
 * @version 1.0
 * @since 20/03/2024
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DispatchControllerE2ETest {

    private String droneRegisterUrl, droneGetUrl, loadMedicationUrl, statusChangeUrl;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    DispatchController dispatchController;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    DroneRepository droneRepository;

    @Autowired
    MedicationRepository medicationRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    void init() {
        cleanup();
        droneRegisterUrl = TestConstants.LOCALHOST + port + TestConstants.REGISTER_DRONE_URL;
        droneGetUrl = TestConstants.LOCALHOST + port + TestConstants.DRONE_GET_URL;
        loadMedicationUrl = TestConstants.LOCALHOST + port + TestConstants.LOAD_MEDICATION_URL;
        statusChangeUrl = TestConstants.LOCALHOST + port + TestConstants.DRONE_STATUS_CHANGE_URL;
    }

    @AfterEach
    void destroy() {
        cleanup();
    }

    /**
     * Clear database table
     */
    private void cleanup() {
        medicationRepository.deleteAll();
        droneRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E Test => Register and get drone - Test happy path")
    void testRegisterDrone_E2ESuccess() throws Exception {
        DroneRequestDTO requestDTO = new DroneRequestDTO("E2E0001", "Cruiserweight", 400, 95);
        ResponseEntity<String> created = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO), String.class);
        JsonNode jsonCreated = mapper.readTree(created.getBody().toString());

        ResponseEntity<String> search =
                testRestTemplate.exchange(droneGetUrl + requestDTO.getSerialNumber(), HttpMethod.GET, setHeaders(requestDTO), String.class);
        JsonNode jsonGet = mapper.readTree(search.getBody().toString());

        Assertions.assertEquals(HttpStatus.OK, created.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, search.getStatusCode());

        Assertions.assertEquals("\"" + requestDTO.getSerialNumber() + "\"", jsonGet.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"" + requestDTO.getModel() + "\"", jsonGet.get("object").get("model").toString());
        Assertions.assertEquals("\"" + requestDTO.getWeight().toString() + "\"", jsonGet.get("object").get("weight").toString());
        Assertions.assertEquals("\"" + requestDTO.getCapacity().toString() + "\"", jsonGet.get("object").get("capacity").toString());
        Assertions.assertEquals("\"IDLE\"", jsonGet.get("object").get("status").toString());

        Assertions.assertEquals("\"" + requestDTO.getSerialNumber() + "\"", jsonCreated.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"" + requestDTO.getModel() + "\"", jsonCreated.get("object").get("model").toString());
        Assertions.assertEquals("\"" + requestDTO.getWeight().toString() + "\"", jsonCreated.get("object").get("weight").toString());
        Assertions.assertEquals("\"" + requestDTO.getCapacity().toString() + "\"", jsonCreated.get("object").get("capacity").toString());
    }

    @Test
    @DisplayName("E2E Test => Get drone information - Test happy path")
    void testLoadMedication_E2ESuccess() throws Exception {
        DroneRequestDTO requestDTO = new DroneRequestDTO("E2E-SN-0001", "Cruiserweight", 400, 95);
        ResponseEntity<String> created = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO), String.class);

        MedicationRequestDTO medicationRequestDTO =
                new MedicationRequestDTO("E2E-MED-01", 350, "MED2001",
                        TestConstants.getImage("JPG"), "E2E-SN-0001");
        ResponseEntity<String> med =
                testRestTemplate.exchange(loadMedicationUrl, HttpMethod.POST, setMedicationHeaders(medicationRequestDTO), String.class);
        JsonNode jsonMed = mapper.readTree(med.getBody().toString());

        Assertions.assertEquals(HttpStatus.OK, med.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK.value(), med.getStatusCode().value());
        Assertions.assertEquals("\"" + AppConstants.LOAD_MEDICATION_SUCCESS + "\"", jsonMed.get("message").toString());

        Assertions.assertEquals("\"" + medicationRequestDTO.getSerialNumber() + "\"", jsonMed.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"" + medicationRequestDTO.getCode() + "\"", jsonMed.get("object").get("code").toString());
        Assertions.assertEquals("\"" + medicationRequestDTO.getName() + "\"", jsonMed.get("object").get("name").toString());
        Assertions.assertEquals("\"" + medicationRequestDTO.getWeight().toString() + "\"", jsonMed.get("object").get("weight").toString());
    }

    @Test
    @DisplayName("E2E Test => Change drone status - Test happy path")
    void testStatusChange_E2ESuccess() throws Exception {
        // Add drone details
        DroneRequestDTO requestDTO = new DroneRequestDTO("E2E-SN-0001", "Cruiserweight", 400, 95);
        ResponseEntity<String> drone = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO), String.class);

        // Load medications
        MedicationRequestDTO medicationRequestDTO =
                new MedicationRequestDTO("E2E-MED-01", 350, "MED2001",
                        TestConstants.getImage("JPG"), "E2E-SN-0001");
        ResponseEntity<String> medication =
                testRestTemplate.exchange(loadMedicationUrl, HttpMethod.POST, setMedicationHeaders(medicationRequestDTO), String.class);

        // Change drone status
        DroneStatusChangeRequestDTO statusChangeRequestDTO =
                new DroneStatusChangeRequestDTO("E2E-SN-0001", "LOADED");
        ResponseEntity<String> status =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO), String.class);

        // Get updated drone
        ResponseEntity<String> updated =
                testRestTemplate.exchange(droneGetUrl + "E2E-SN-0001", HttpMethod.GET, setHeaders(requestDTO), String.class);

        Assertions.assertEquals(HttpStatus.OK, drone.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, medication.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, status.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, updated.getStatusCode());

        JsonNode jsonStatus = mapper.readTree(status.getBody().toString());
        Assertions.assertEquals("\"" + AppConstants.DRONE_STATUS_CHANGE_SUCCESS + "E2E-SN-0001\"", jsonStatus.get("message").toString());
        Assertions.assertEquals("\"E2E-SN-0001\"", jsonStatus.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"LOADED\"", jsonStatus.get("object").get("status").toString());

        JsonNode jsonUpdated = mapper.readTree(updated.getBody().toString());
        Assertions.assertEquals("\"" + AppConstants.DRONE_INFO + "\"", jsonUpdated.get("message").toString());
        Assertions.assertEquals("\"E2E-SN-0001\"", jsonUpdated.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"LOADED\"", jsonUpdated.get("object").get("status").toString());

    }

    private HttpEntity setHeaders(DroneRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(request, headers);
        return entity;
    }

    private HttpEntity setMedicationHeaders(MedicationRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(request, headers);
        return entity;
    }

    private HttpEntity setStatusChangeHeaders(DroneStatusChangeRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(request, headers);
        return entity;
    }
}
