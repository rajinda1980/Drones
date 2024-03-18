package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

import java.io.Serializable;

/**
 * Error detail information
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@Setter
public class ErrorDetailHeaderDTO implements Serializable {

    @JsonProperty("message")
    private String message;
}
