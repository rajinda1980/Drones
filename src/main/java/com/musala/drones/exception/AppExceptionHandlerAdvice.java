package com.musala.drones.exception;

import com.musala.drones.dto.ErrorDetailHeaderDTO;
import com.musala.drones.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
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
        ErrorDetailHeaderDTO headerDTO = new ErrorDetailHeaderDTO();
        headerDTO.setMessage(exception.getMessage());
        List<ErrorDetailHeaderDTO> errors = new ArrayList<>();
        errors.add(headerDTO);

        ErrorResponseDTO responseDTO = constructErrorResponse(errors, HttpStatus.INTERNAL_SERVER_ERROR, "Caching");
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DroneRegistrationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDroneRegistrationException(DroneRegistrationException exception, WebRequest request) {
        ErrorDetailHeaderDTO headerDTO = new ErrorDetailHeaderDTO();
        headerDTO.setMessage(exception.getMessage());
        List<ErrorDetailHeaderDTO> errors = new ArrayList<>();
        errors.add(headerDTO);

        ErrorResponseDTO responseDTO =
                constructErrorResponse(errors, HttpStatus.BAD_REQUEST, ((ServletWebRequest) request).getRequest().getServletPath());
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);
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
