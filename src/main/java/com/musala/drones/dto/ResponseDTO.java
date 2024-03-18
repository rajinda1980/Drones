package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Response class to user request
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO implements Serializable {

    @JsonProperty("createdDate")
    private LocalDateTime createdDate;

    @JsonProperty("statusCode")
    private int statusCode;

    @JsonProperty("statusValue")
    private String statusValue;

    @JsonProperty("message")
    private String message;

    @JsonProperty("object")
    private Object object;
}
