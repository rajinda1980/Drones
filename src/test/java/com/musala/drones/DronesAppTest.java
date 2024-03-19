package com.musala.drones;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.filter.CorsFilter;

/**
 * Unit test for main class
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@SpringBootTest
public class DronesAppTest {

    @Autowired
    private CorsFilter corsFilter;

    @Test
    void contextLoader() {
        Assertions.assertNotNull(corsFilter);
    }
}
