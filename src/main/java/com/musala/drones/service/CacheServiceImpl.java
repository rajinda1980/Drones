package com.musala.drones.service;

import com.musala.drones.datamodel.data.Model;
import com.musala.drones.datamodel.data.State;
import com.musala.drones.datamodel.repository.ModelRepository;
import com.musala.drones.datamodel.repository.StateRepository;
import com.musala.drones.exception.CacheException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache service implementation class
 *
 * @author Rajinda
 * @version 1.0
 * @since 18/03/2024
 */
@Service
public class CacheServiceImpl implements CacheService {

    // Stateful property to hold master data
    Map<String, Model> droneModels = new ConcurrentHashMap<>();
    Map<String, State> droneState = new ConcurrentHashMap<>();

    private ModelRepository modelRepository;
    private StateRepository stateRepository;

    public CacheServiceImpl(ModelRepository modelRepository, StateRepository stateRepository) {
        this.modelRepository = modelRepository;
        this.stateRepository = stateRepository;
    }

    /**
     * To retain values stored in the database
     *
     * @return map. Key - model, Value - Primary key
     * @throws CacheException
     */
    public Map<String, Model> getDroneModels() throws CacheException {
        if (droneModels.isEmpty()) {
            List<Model> models = modelRepository.findAll();
            models.stream().forEach(
                m -> droneModels.put(m.getCategory(), new Model(m.getDid(), m.getCategory()))
            );
        }
        return droneModels;
    }

    /**
     * To retain values stored in the database
     *
     * @return map. Key - State, Value - Primary Key
     * @throws CacheException
     */
    public Map<String, State> getDroneStates() throws CacheException {
        if (droneState.isEmpty()) {
            List<State> states = stateRepository.findAll();
            states.stream().forEach(
                s -> droneState.put(s.getStatus(), new State(s.getDid(), s.getStatus()))
            );
        }
        return droneState;
    }
}
