package com.musala.drones.controller;

import com.musala.drones.dto.*;
import com.musala.drones.service.DroneService;
import com.musala.drones.service.MedicationService;
import com.musala.drones.util.AppValidator;
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
@RequestMapping("/v1/api/")
@Slf4j
public class DispatchController {

    private DroneService droneService;
    private AppValidator appValidator;
    private MedicationService medicationService;

    public DispatchController(DroneService service, AppValidator appValidator, MedicationService medicationService) {
        this.droneService = service;
        this.appValidator = appValidator;
        this.medicationService = medicationService;
    }

    /**
     * Registering drone in the system
     *
     * @param droneRequest - Drone request details
     * @return success message if drone is added to the system
     */
    @PostMapping("drone/register")
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
    @GetMapping("drone/get/{sn}")
    public ResponseEntity<DroneDTO> getDrone(@PathVariable String sn) {
        DroneDTO droneDTO = droneService.getDrone(sn);
        return new ResponseEntity<>(droneDTO, HttpStatus.OK);
    }

    /**
     * Load medication
     *
     * @param medicationRequestDTO
     * @return success message
     */
    @PostMapping("medication/load")
    public ResponseEntity<ResponseDTO> loadMedication(@Validated @RequestBody MedicationRequestDTO medicationRequestDTO) {
        appValidator.validateImageSignature(medicationRequestDTO.getImage());
        ResponseDTO responseDTO = medicationService.loadMedication(medicationRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Change drone status
     *
     * @param droneStatusChangeRequestDTO
     * @return success message
     */
    @PutMapping("drone/status")
    public ResponseEntity<?> changeStatus(@Validated @RequestBody DroneStatusChangeRequestDTO droneStatusChangeRequestDTO) {
        appValidator.validateDroneStatus(droneStatusChangeRequestDTO.getStatus());
        ResponseDTO responseDTO = droneService.changeStatus(droneStatusChangeRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
