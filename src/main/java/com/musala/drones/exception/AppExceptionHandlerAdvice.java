package com.musala.drones.exception;

import com.musala.drones.dto.ErrorDetailHeaderDTO;
import com.musala.drones.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Exception handler class for application custom exceptions
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@RestControllerAdvice
@Slf4j
public class AppExceptionHandlerAdvice {

    @ExceptionHandler(value = CacheException.class)
    public ResponseEntity<ErrorResponseDTO> handleCacheException(CacheException exception) {
        ErrorResponseDTO responseDTO = getErrorResponseDTO(exception, null, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DroneRegistrationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDroneRegistrationException(DroneRegistrationException exception, WebRequest request) {
        ErrorResponseDTO responseDTO = getErrorResponseDTO(exception, request, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DroneSearchException.class)
    public ResponseEntity<ErrorResponseDTO> handleDroneSearchException(DroneRegistrationException exception, WebRequest request) {
        ErrorResponseDTO responseDTO = getErrorResponseDTO(exception, request, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ImageSignatureException.class)
    public ResponseEntity<ErrorResponseDTO> handleImageSignatureException(ImageSignatureException exception, WebRequest request) {
        ErrorResponseDTO responseDTO = getErrorResponseDTO(exception, request, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = LoadMedicationException.class)
    public ResponseEntity<ErrorResponseDTO> handleLoadMedicationException(LoadMedicationException exception, WebRequest request) {
        ErrorResponseDTO responseDTO = getErrorResponseDTO(exception, request, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DroneStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleDroneStatusException(DroneStatusException exception, WebRequest request) {
        ErrorResponseDTO responseDTO = getErrorResponseDTO(exception, request, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DroneModelException.class)
    public ResponseEntity<ErrorResponseDTO> handleDroneModelException(DroneModelException exception, WebRequest request) {
        ErrorResponseDTO responseDTO = getErrorResponseDTO(exception, request, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private ErrorResponseDTO getErrorResponseDTO(RuntimeException exception, WebRequest request, HttpStatus status) {
        ErrorDetailHeaderDTO headerDTO = new ErrorDetailHeaderDTO();
        headerDTO.setMessage(exception.getMessage());
        List<ErrorDetailHeaderDTO> errors = new ArrayList<>();
        errors.add(headerDTO);

        String path;
        if (null == request) {
            path = "Application";
        } else {
            path = null != ((ServletWebRequest) request).getRequest().getPathInfo() ?
                    ((ServletWebRequest) request).getRequest().getPathInfo() : ((ServletWebRequest) request).getRequest().getServletPath();
        }

        ErrorResponseDTO responseDTO = constructErrorResponse(errors, status, path);
        return responseDTO;
    }

    private ErrorResponseDTO constructErrorResponse(List<? extends Object> messages, HttpStatus status, String path) {
        ErrorResponseDTO responseDTO =
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        status.value(),
                        messages,
                        path
                );
        return responseDTO;

    }
}
