package com.musala.drones.exception;

/**
 * Exceptions related to invalid image signature
 *
 * @author Rajinda
 * @version 1.0
 * @since 20/03/2024
 */
public class ImageSignatureException extends RuntimeException {
    public ImageSignatureException(String msg) {
        super(msg);
    }
}
