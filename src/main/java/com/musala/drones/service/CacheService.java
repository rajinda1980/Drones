package com.musala.drones.service;

import com.musala.drones.datamodel.data.Model;
import com.musala.drones.datamodel.data.State;
import com.musala.drones.exception.CacheException;

import java.util.Map;

/**
 * A cache interface to expose methods
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
public interface CacheService {

    public Map<String, Model> getDroneModels() throws CacheException;
    public Map<String, State> getDroneStates() throws CacheException;
}
