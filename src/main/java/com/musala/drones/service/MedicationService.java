package com.musala.drones.service;

import com.musala.drones.dto.MedicationRequestDTO;
import com.musala.drones.dto.ResponseDTO;
import com.musala.drones.exception.LoadMedicationException;

/**
 * A service interface to medication related services
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
public interface MedicationService {

    ResponseDTO loadMedication(MedicationRequestDTO medicationRequestDTO) throws LoadMedicationException;
}
