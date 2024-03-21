package com.musala.drones.util;

import com.musala.drones.exception.DroneModelException;
import com.musala.drones.exception.DroneStatusException;
import com.musala.drones.exception.ImageSignatureException;
import com.musala.drones.service.CacheService;
import org.springframework.stereotype.Component;

/**
 * Custom application validations
 *
 * @author Rajinda
 * @version 1.0
 * @since 20/03/2024
 */
@Component
public class AppValidator {

    private CacheService cacheService;

    public AppValidator(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Validate image signature. Valid signatures are PNG, JPG and JPEG
     *
     * @param image
     * @return true/false
     * @throws ImageSignatureException
     */
    public void validateImageSignature(byte[] image) throws ImageSignatureException {
        if (image.length >= 4) {
            boolean validImageType = false;

            // Validate for PNG type
            if (image[0] == (byte) 0x89 && image[1] == (byte) 0x50 && image[2] == (byte) 0x4E && image[3] == (byte) 0x47) {
                validImageType = true;

            } else if (image[0] == (byte) 0xFF && image[1] == (byte) 0xD8 && image[2] == (byte) 0xFF) {
                // Validate for JPEG image type
                if (image.length >= 6 &&
                        image[6] == (byte) 0x45 && image[7] == (byte) 0x78 &&
                        image[8] == (byte) 0x69 && image[9] == (byte) 0x66 &&
                        image[10] == (byte) 0x00 && image[11] == (byte) 0x00) {
                    validImageType = true;

                } else {
                    // This is JPG image
                    validImageType = true;
                }
            }

            if (!validImageType) {
                throw new ImageSignatureException(AppConstants.INVALID_IMAGE_TYPE);
            }

        } else {
            throw new ImageSignatureException(AppConstants.UNKNOWN_IMAGE_TYPE);
        }
    }

    /**
     * Validate given drone status
     *
     * @param status
     * @return true/false
     * @throws DroneStatusException
     */
    public boolean validateDroneStatus(String status) throws DroneStatusException {
        if (cacheService.getDroneStates().containsKey(status)) {
            return true;
        }
        throw new DroneStatusException(AppConstants.INVALID_DRONE_STATUS + cacheService.getDroneStates().keySet());
    }

    /**
     * Validate drone model
     *
     * @param model
     * @return true/false
     * @throws DroneModelException
     */
    public boolean validateDroneModel(String model) throws DroneModelException {
        if (cacheService.getDroneModels().containsKey(model)) {
            return true;
        }
        throw new DroneModelException(AppConstants.INVALID_MODEL + cacheService.getDroneModels().keySet());
    }
}
