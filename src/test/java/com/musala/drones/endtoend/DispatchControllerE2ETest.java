package com.musala.drones.endtoend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.controller.DispatchController;
import com.musala.drones.datamodel.repository.DroneRepository;
import com.musala.drones.datamodel.repository.MedicationRepository;
import com.musala.drones.dto.DroneRequestDTO;
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

    private String droneRegisterUrl;
    private String droneGetUrl;
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
    @DisplayName("Register drone - Test happy path")
    void testRegisterDrone_E2ESuccess() throws Exception {
        DroneRequestDTO requestDTO = new DroneRequestDTO("E2E0001", "Cruiserweight", 400, 95);
        ResponseEntity<String> created = testRestTemplate.exchange(droneRegisterUrl, HttpMethod.POST,  setHeaders(requestDTO), String.class);
        JsonNode jsonCreated = mapper.readTree(created.getBody().toString());

        ResponseEntity<String> search =
                testRestTemplate.exchange(droneGetUrl + requestDTO.getSerialNumber(), HttpMethod.GET, setHeaders(requestDTO), String.class);
        JsonNode jsonGet = mapper.readTree(search.getBody().toString());

        Assertions.assertEquals(HttpStatus.OK, created.getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, search.getStatusCode());

        Assertions.assertEquals("\"" + requestDTO.getSerialNumber() + "\"", jsonGet.get("serialNumber").toString());
        Assertions.assertEquals("\"" + requestDTO.getModel() + "\"", jsonGet.get("model").toString());
        Assertions.assertEquals(requestDTO.getWeight().toString(), jsonGet.get("weight").toString());
        Assertions.assertEquals(requestDTO.getCapacity().toString(), jsonGet.get("capacity").toString());
        Assertions.assertEquals("\"IDLE\"", jsonGet.get("status").toString());

        Assertions.assertEquals("\"" + requestDTO.getSerialNumber() + "\"", jsonCreated.get("object").get("serialNumber").toString());
        Assertions.assertEquals("\"" + requestDTO.getModel() + "\"", jsonCreated.get("object").get("model").toString());
        Assertions.assertEquals("\"" + requestDTO.getWeight().toString() + "\"", jsonCreated.get("object").get("weight").toString());
        Assertions.assertEquals("\"" + requestDTO.getCapacity().toString() + "\"", jsonCreated.get("object").get("capacity").toString());
    }

    private HttpEntity setHeaders(DroneRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, AppConstants.CONTENT_TYPE);
        HttpEntity entity = new HttpEntity(request, headers);
        return entity;
    }

}
