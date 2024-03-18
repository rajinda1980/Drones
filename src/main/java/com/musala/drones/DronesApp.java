package com.musala.drones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main class of the application
 *
 * @author Rajinda
 * @version 1.0
 * @since 14/03/2024
 */
@SpringBootApplication
@ComponentScan({"com.musala.drones", "com.musala.drones.datamodel"})
public class DronesApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(DronesApp.class);
    }

}
