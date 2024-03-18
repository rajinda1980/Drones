package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response generated for exception scenarios
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@AllArgsConstructor
public class ErrorResponseDTO implements Serializable {

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("status")
    private int status;

    @JsonProperty("detail")
    private List<? extends Object> detail;

    @JsonProperty("path")
    private String path;
}
