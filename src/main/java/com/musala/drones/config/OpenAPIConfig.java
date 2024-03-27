package com.musala.drones.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * FSwagger configuration class
 *
 * @author Rajinda
 * @version 1.0
 * @since 27/03/2024
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Drone Application", version = "1.0", description = "Drone Application"))
public class OpenAPIConfig {
}
