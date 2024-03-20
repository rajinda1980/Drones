package com.musala.drones.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Wiremock configuration beans
 *
 * @author Rajinda
 * @version 1.0
 * @since 19/03/2024
 */
@Configuration
public class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer webServer() {
        return new WireMockServer(options().dynamicPort());
    }
}
