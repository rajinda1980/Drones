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

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;

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

    private String droneRegisterUrl, droneGetUrl, loadMedicationUrl, statusChangeUrl, loadedMedicationUrl, availableDronesUrl, batteryLevelUrl;

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
        loadedMedicationUrl = TestConstants.LOCALHOST + port + TestConstants.LOADED_MEDICATION_ITEM_URL;
        availableDronesUrl = TestConstants.LOCALHOST + port + TestConstants.FIND_IDLE_DRONES;
        batteryLevelUrl = TestConstants.LOCALHOST + port + TestConstants.GET_BATTERY_LEVEL;
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

    @Test
    @DisplayName("E2E Test => Find loaded medications, available drones and battery level - Test happy path")
    void  testFindLoadedMedicationItems_success() throws Exception {
        // Add drone details
        DroneRequestDTO requestDTO1 = new DroneRequestDTO("E2E-SN-0001", "Lightweight", 100, 95);
        DroneRequestDTO requestDTO2 = new DroneRequestDTO("E2E-SN-0002", "Middleweight", 300, 75);
        DroneRequestDTO requestDTO3 = new DroneRequestDTO("E2E-SN-0003", "Cruiserweight", 400, 80);
        DroneRequestDTO requestDTO4 = new DroneRequestDTO("E2E-SN-0004", "Lightweight", 150, 20);
        DroneRequestDTO requestDTO5 = new DroneRequestDTO("E2E-SN-0005", "Heavyweight", 500, 100);

        ResponseEntity<String> drone1 = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO1), String.class);
        ResponseEntity<String> drone2 = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO2), String.class);
        ResponseEntity<String> drone3 = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO3), String.class);
        ResponseEntity<String> drone4 = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO4), String.class);
        ResponseEntity<String> drone5 = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO5), String.class);

        // Load medications
        MedicationRequestDTO medicationRequestDT01 =
                new MedicationRequestDTO("E2E-MED-01", 250, "MED2001", TestConstants.getImage("JPG"), "E2E-SN-0003");
        ResponseEntity<String> medication1 =
                testRestTemplate.exchange(loadMedicationUrl, HttpMethod.POST, setMedicationHeaders(medicationRequestDT01), String.class);
        DroneStatusChangeRequestDTO statusChangeRequestDTO1 = new DroneStatusChangeRequestDTO("E2E-SN-0003", "LOADED");
        ResponseEntity<String> status1 =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO1), String.class);
        DroneStatusChangeRequestDTO statusChangeRequestDTO2 = new DroneStatusChangeRequestDTO("E2E-SN-0003", "IDLE");
        ResponseEntity<String> status2 =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO2), String.class);

        MedicationRequestDTO medicationRequestDTO2 =
                new MedicationRequestDTO("E2E-MED-02", 150, "MED2001", TestConstants.getImage("PNG"), "E2E-SN-0003");
        ResponseEntity<String> medication2 =
                testRestTemplate.exchange(loadMedicationUrl, HttpMethod.POST, setMedicationHeaders(medicationRequestDTO2), String.class);
        DroneStatusChangeRequestDTO statusChangeRequestDTO3 = new DroneStatusChangeRequestDTO("E2E-SN-0003", "LOADED");
        ResponseEntity<String> status3 =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO3), String.class);
        DroneStatusChangeRequestDTO statusChangeRequestDTO4 = new DroneStatusChangeRequestDTO("E2E-SN-0003", "IDLE");
        ResponseEntity<String> status4 =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO4), String.class);

        MedicationRequestDTO medicationRequestDTO3 =
                new MedicationRequestDTO("E2E-MED-03", 300, "MED2001", TestConstants.getImage("PNG"), "E2E-SN-0003");
        ResponseEntity<String> medication3 =
                testRestTemplate.exchange(loadMedicationUrl, HttpMethod.POST, setMedicationHeaders(medicationRequestDTO3), String.class);
        DroneStatusChangeRequestDTO statusChangeRequestDTO5 = new DroneStatusChangeRequestDTO("E2E-SN-0003", "LOADED");
        ResponseEntity<String> status5 =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO5), String.class);

        MedicationRequestDTO medicationRequestDTO4 =
                new MedicationRequestDTO("E2E-MED-04", 400, "MED2001", TestConstants.getImage("JPEG"), "E2E-SN-0005");
        ResponseEntity<String> medication4 =
                testRestTemplate.exchange(loadMedicationUrl, HttpMethod.POST, setMedicationHeaders(medicationRequestDTO4), String.class);
        DroneStatusChangeRequestDTO statusChangeRequestDTO7 = new DroneStatusChangeRequestDTO("E2E-SN-0005", "LOADED");
        ResponseEntity<String> status7 =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO7), String.class);
        DroneStatusChangeRequestDTO statusChangeRequestDTO8 = new DroneStatusChangeRequestDTO("E2E-SN-0005", "IDLE");
        ResponseEntity<String> status8 =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO8), String.class);

        MedicationRequestDTO medicationRequestDTO5 =
                new MedicationRequestDTO("E2E-MED-05", 375, "MED2001", TestConstants.getImage("JPG"), "E2E-SN-0005");
        ResponseEntity<String> medication5 =
                testRestTemplate.exchange(loadMedicationUrl, HttpMethod.POST, setMedicationHeaders(medicationRequestDTO5), String.class);
        DroneStatusChangeRequestDTO statusChangeRequestDTO9 = new DroneStatusChangeRequestDTO("E2E-SN-0005", "LOADED");
        ResponseEntity<String> status9 =
                testRestTemplate.exchange(statusChangeUrl, HttpMethod.PUT, setStatusChangeHeaders(statusChangeRequestDTO9), String.class);


        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(headers);

        // Get loaded medications
        ResponseEntity<List> loadedMedications = testRestTemplate.exchange(loadedMedicationUrl + "E2E-SN-0003", HttpMethod.GET, entity, List.class);
        // Assert loaded medications
        Assertions.assertEquals(HttpStatus.OK, loadedMedications.getStatusCode());
        ArrayList listMedications = (ArrayList) loadedMedications.getBody();
        Assertions.assertTrue(listMedications.size() == 3);
        String encodeJpg = Base64.getEncoder().encodeToString(TestConstants.getImage("JPG"));
        String encodePng = Base64.getEncoder().encodeToString(TestConstants.getImage("PNG"));
        for (int i = 0; i < 3; i++) {
            LinkedHashMap map = (LinkedHashMap)listMedications.get(i);
            Assertions.assertTrue(map.get("serialNumber").equals("E2E-SN-0003"));
            Assertions.assertTrue(map.get("name").toString().matches("^(E2E-MED-01|E2E-MED-02|E2E-MED-03)$"));
            Assertions.assertTrue(map.get("code").toString().equals("MED2001"));
            Assertions.assertTrue(map.get("weight").toString().matches("^(150|250|300)$"));
            Assertions.assertTrue(map.get("image").equals(encodeJpg) || map.get("image").equals(encodePng));
        }

        // Get idle drones
        ResponseEntity<List> idleDrones = testRestTemplate.exchange(availableDronesUrl, HttpMethod.GET, entity, List.class);
        // Assert idle drones
        Assertions.assertEquals(HttpStatus.OK, idleDrones.getStatusCode());
        ArrayList listIdleDrones = (ArrayList) idleDrones.getBody();
        Assertions.assertTrue(listIdleDrones.size() == 3);
        for (int i = 0; i < 3; i++) {
            LinkedHashMap map = (LinkedHashMap)listIdleDrones.get(i);
            Assertions.assertTrue(map.get("serialNumber").toString().matches("^(E2E-SN-0001|E2E-SN-0002|E2E-SN-0004)$"));
            Assertions.assertTrue(map.get("model").toString().matches("^(Lightweight|Middleweight)$"));
            Assertions.assertTrue(map.get("weight").toString().matches("^(100|150|300)$"));
            Assertions.assertTrue(map.get("capacity").toString().matches("^(20|75|95)$"));
        }

        // Get battery capacity
        ResponseEntity<Integer> bCapacity= testRestTemplate.exchange(batteryLevelUrl + "E2E-SN-0003", HttpMethod.GET, entity, Integer.class);
        Assertions.assertEquals(HttpStatus.OK, bCapacity.getStatusCode());
        Assertions.assertEquals("80", bCapacity.getBody().toString());
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
