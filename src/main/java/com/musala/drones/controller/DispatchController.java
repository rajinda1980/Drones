package com.musala.drones.controller;

import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller class is utilized to expose methods for registering drones within the system
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@RestController
@RequestMapping("/v1/api/drone/")
@Slf4j
public class DispatchController {

    private RegistrationService registrationService;

    public DispatchController(RegistrationService service) {
        this.registrationService = service;
    }

    /**
     * Registering drone in the system
     *
     * @param droneRequest - Drone request details
     * @return success message if drone is added to the system
     */
    @PostMapping("register")
    public ResponseEntity<ResponseDTO> registerDrone(@Validated @RequestBody DroneRequestDTO droneRequest) {
        ResponseDTO responseDTO = registrationService.registerDrone(droneRequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
