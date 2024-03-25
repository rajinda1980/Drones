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

import java.util.List;

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
        appValidator.validateDroneModel(droneRequest.getModel());
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
    public ResponseEntity<ResponseDTO> getDrone(@PathVariable String sn) {
        ResponseDTO responseDTO = droneService.getDrone(sn);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
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
    public ResponseEntity<ResponseDTO> changeStatus(@Validated @RequestBody DroneStatusChangeRequestDTO droneStatusChangeRequestDTO) {
        appValidator.validateDroneStatus(droneStatusChangeRequestDTO.getStatus());
        ResponseDTO responseDTO = droneService.changeStatus(droneStatusChangeRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Find loaded medication items
     *
     * @param sn - drone serial number
     * @return loaded medication item list
     */
    @GetMapping("medication/find/{sn}")
    public ResponseEntity<List<LoadedMedicationItemDTO>> findLoadedMedicationItems(@PathVariable String sn) {
        List<LoadedMedicationItemDTO> list = medicationService.findLoadedMedicationItems(sn);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * Find drones in idle status
     *
     * @return available drone list
     */
    @GetMapping("drone/find/idle")
    public ResponseEntity<List<AvailableDroneDTO>> findAvailableDrones() {
        List<AvailableDroneDTO> drones = droneService.findIdleDrones();
        return new ResponseEntity<>(drones, HttpStatus.OK);
    }

    /**
     * Get drone battery level
     *
     * @param sn - Drone serial number
     * @return drone battery level
     */
    @GetMapping("drone/battery-level/{sn}")
    public ResponseEntity<Integer> getDroneBatteryLevel(@PathVariable String sn) {
        Integer level = droneService.getDroneBatteryLevel(sn);
        return new ResponseEntity<>(level, HttpStatus.OK);
    }
}
