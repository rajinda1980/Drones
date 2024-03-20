package com.musala.drones.controller;

import com.musala.drones.dto.DroneDTO;
import com.musala.drones.dto.DroneRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.service.DroneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Dispatch class
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@RestController
@RequestMapping("/v1/api/drone/")
@Slf4j
public class DispatchController {

    private DroneService droneService;

    public DispatchController(DroneService service) {
        this.droneService = service;
    }

    /**
     * Registering drone in the system
     *
     * @param droneRequest - Drone request details
     * @return success message if drone is added to the system
     */
    @PostMapping("register")
    public ResponseEntity<ResponseDTO> registerDrone(@Validated @RequestBody DroneRequestDTO droneRequest) {
        ResponseDTO responseDTO = droneService.registerDrone(droneRequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Search drone to given serial number
     *
     * @param sn - Serial number
     * @return DroneDTO
     */
    @GetMapping("get/{sn}")
    public ResponseEntity<DroneDTO> getDrone(@PathVariable String sn) {
        DroneDTO droneDTO = droneService.getDrone(sn);
        return new ResponseEntity<>(droneDTO, HttpStatus.OK);
    }
}
