package com.musala.drones.service;

import com.musala.drones.dto.LoadedMedicationItemDTO;
import com.musala.drones.dto.MedicationRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.LoadMedicationException;

import java.util.List;

/**
 * A service interface to medication related services
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
public interface MedicationService {

    ResponseDTO loadMedication(MedicationRequestDTO medicationRequestDTO) throws LoadMedicationException;
    List<LoadedMedicationItemDTO> findLoadedMedicationItems(String serialNumber) throws LoadMedicationException;
}
