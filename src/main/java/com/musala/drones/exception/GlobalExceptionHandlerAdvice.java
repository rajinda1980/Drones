package com.musala.drones.exception;

import com.musala.drones.dto.ErrorDetailDTO;
import com.musala.drones.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Exception handler class for global exceptions
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<FieldError> errors = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors();

        List<ErrorDetailDTO> detailList = new ArrayList<>();
        for (FieldError error : errors) {
            ErrorDetailDTO errorDetail = new ErrorDetailDTO();
            errorDetail.setFieldName(error.getField());
            errorDetail.setMessage(error.getDefaultMessage());
            errorDetail.setFieldValue(error.getRejectedValue());
            detailList.add(errorDetail);
        }

        String path = null != ((ServletWebRequest) request).getRequest().getPathInfo() ?
                ((ServletWebRequest) request).getRequest().getPathInfo() : ((ServletWebRequest) request).getRequest().getServletPath();

        ErrorResponseDTO responseDTO =
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        detailList,
                        path);
        return new ResponseEntity<>(responseDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);

    }
}
