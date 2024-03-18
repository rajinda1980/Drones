package com.musala.drones.config;

import com.musala.drones.util.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter class for request content type
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@Component
@Slf4j
public class ContentTypeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        if (StringUtils.isBlank(contentType) || !contentType.equalsIgnoreCase(AppConstants.CONTENT_TYPE)) {
            log.info("Invalid request is received. Content-Type : {}", contentType);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Invalid Content-Type. Content-Type must be application/json");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
