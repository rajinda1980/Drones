package com.musala.drones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Error detail information
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@Setter
public class ErrorDetailDTO extends ErrorDetailHeaderDTO implements Serializable {

    @JsonProperty("fieldName")
    private String fieldName;

    @JsonProperty("fieldValue")
    private Object fieldValue;
}
