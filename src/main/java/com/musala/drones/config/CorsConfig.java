package com.musala.drones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configure CORS configuration
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@Configuration
public class CorsConfig {

    /**
     * CORS bean configuration
     *
     * @return CORS filter
     */
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList(HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE));
        corsConfiguration.setAllowedMethods(
                Arrays.asList(
                        HttpMethod.POST.name(),
                        HttpMethod.GET.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name()
                )
        );

        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
