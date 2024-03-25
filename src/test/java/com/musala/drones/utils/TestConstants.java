package com.musala.drones.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.musala.drones.datamodel.data.Model;
import com.musala.drones.datamodel.data.State;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store and manage test related constant values
 *
 * @author Rajinda
 * @version 1.0
 * @since 19/03/2024
 */
@NoArgsConstructor
@Component
public final class TestConstants {

    public static final String LOCALHOST = "http://localhost:";
    public static final String REGISTER_DRONE_URL = "/v1/api/drone/register";
    public static final String LOAD_MEDICATION_URL = "/v1/api/medication/load";
    public static final String DRONE_GET_URL = "/v1/api/drone/get/";
    public static final String CHARSET_FOR_FILE_TRANSFORM = "UTF-8";
    public static final String INVALID_DRONE_MODEL = "The model must be one of the following values: [Middleweight, Cruiserweight, Lightweight, Heavyweight]";
    public static final String DRONE_STATUS_CHANGE_URL = "/v1/api/drone/status";

    // Wiremock request json file names
    public static final String DRONE_REGISTRATION_REQUEST_JSON_SUCCESS = "/assetsTestFiles/request/drone_registration_request_success.json";
    public static final String DRONE_REGISTRATION_REQUEST_JSON_ALREADY_REGISTERED = "/assetsTestFiles/request/drone_registration_request_registered.json";
    public static final String DRONE_REGISTRATION_REQUEST_JSON_INVALID_SERIALNUMBER = "/assetsTestFiles/request/drone_registration_request_invalid_serialnumber.json";
    public static final String DRONE_REGISTRATION_REQUEST_JSON_INVALID_MODEL = "/assetsTestFiles/request/drone_registration_request_invalid_model.json";
    public static final String DRONE_REGISTRATION_REQUEST_JSON_WEIGHT_LOW = "/assetsTestFiles/request/drone_registration_request_weight_low.json";
    public static final String DRONE_REGISTRATION_REQUEST_JSON_WEIGHT_HIGH = "/assetsTestFiles/request/drone_registration_request_weight_high.json";
    public static final String DRONE_REGISTRATION_REQUEST_JSON_CAPACITY_LOW = "/assetsTestFiles/request/drone_registration_request_capacity_low.json";
    public static final String DRONE_REGISTRATION_REQUEST_JSON_CAPACITY_HIGH = "/assetsTestFiles/request/drone_registration_request_capacity_high.json";
    public static final String DRONE_REGISTRATION_REQUEST_JSON_INVALID_WEIGHT_CAPACITY = "/assetsTestFiles/request/drone_registration_request_invalid_weight_capacity.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_SUCCESS = "/assetsTestFiles/request/load_medication_request_success.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_CODE_MANDATORY = "/assetsTestFiles/request/load_medication_request_code_mandatory.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_DRONE_NOT_OCCUPIED = "/assetsTestFiles/request/load_medication_request_drone_not_occupied.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_EXCEED_WEIGHT = "/assetsTestFiles/request/load_medication_request_exceed_weight.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_INVALID_CODE = "/assetsTestFiles/request/load_medication_request_invalid_code.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_INVALID_NAME = "/assetsTestFiles/request/load_medication_request_invalid_name.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_INVALID_SERIALNUMBER = "/assetsTestFiles/request/load_medication_request_invalid_serialnumber.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_LOW_CAPACITY = "/assetsTestFiles/request/load_medication_request_low_capacity.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_WEIGHT_MANDATORY = "/assetsTestFiles/request/load_medication_request_weight_mandatory.json";
    public static final String LOAD_MEDICATION_REQUEST_JSON_WITHOUT_NAME = "/assetsTestFiles/request/load_medication_request_without_name.json";
    public static final String DRONE_STATUS_CHANGE_REQUEST_JSON_SUCCESS = "/assetsTestFiles/request/drone_status_change_request_success.json";
    public static final String DRONE_STATUS_CHANGE_REQUEST_JSON_INVALID_SERIALNUMBER = "/assetsTestFiles/request/drone_status_change_request_invalid_serialnumber.json";

    // Wiremock response json file names
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_SUCCESS = "/assetsTestFiles/response/drone_registration_response_success.json";
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_ALREADY_REGISTERED = "/assetsTestFiles/response/drone_registration_response_registered.json";
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_INVALID_SERIALNUMBER = "/assetsTestFiles/response/drone_registration_response_invalid_serialnumber.json";
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_INVALID_MODEL = "/assetsTestFiles/response/drone_registration_response_invalid_model.json";
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_WEIGHT_LOW = "/assetsTestFiles/response/drone_registration_response_weight_low.json";
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_WEIGHT_HIGH = "/assetsTestFiles/response/drone_registration_response_weight_high.json";
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_CAPACITY_LOW = "/assetsTestFiles/response/drone_registration_response_capacity_low.json";
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_CAPACITY_HIGH = "/assetsTestFiles/response/drone_registration_response_capacity_high.json";
    public static final String DRONE_REGISTRATION_RESPONSE_JSON_INVALID_WEIGHT_CAPACITY = "/assetsTestFiles/response/drone_registration_response_invalid_weight_capacity.json";
    public static final String GET_DRONE_RESPONSE_JSON_SUCCESS = "/assetsTestFiles/response/get_drone_response_success.json";
    public static final String GET_DRONE_RESPONSE_JSON_NOT_EXIST = "/assetsTestFiles/response/get_drone_response_drone_not_exist.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_SUCCESS = "/assetsTestFiles/response/load_medication_response_success.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_CODE_MANDATORY = "/assetsTestFiles/response/load_medication_response_code_mandatory.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_DRONE_NOT_OCCUPIED = "/assetsTestFiles/response/load_medication_response_drone_not_occupied.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_EXCEED_WEIGHT = "/assetsTestFiles/response/load_medication_response_exceed_weight.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_INVALID_CODE = "/assetsTestFiles/response/load_medication_response_invalid_code.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_INVALID_NAME = "/assetsTestFiles/response/load_medication_response_invalid_name.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_INVALID_SERIALNUMBER = "/assetsTestFiles/response/load_medication_response_invalid_serialnumber.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_LOW_CAPACITY = "/assetsTestFiles/response/load_medication_response_low_capacity.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_WEIGHT_MANDATORY = "/assetsTestFiles/response/load_medication_response_weight_mandatory.json";
    public static final String LOAD_MEDICATION_RESPONSE_JSON_WITHOUT_NAME = "/assetsTestFiles/response/load_medication_response_without_name.json";
    public static final String DRONE_STATUS_CHANGE_RESPONSE_JSON_SUCCESS = "/assetsTestFiles/response/drone_status_change_response_success.json";
    public static final String DRONE_STATUS_CHANGE_RESPONSE_JSON_INVALID_SERIALNUMBER = "/assetsTestFiles/response/drone_status_change_response_invalid_serialnumber.json";

    // Images
    public static final String IMAGE_PNG = "image/image1.png";
    public static final String IMAGE_JPEG = "image/image2.jpeg";
    public static final String IMAGE_JPG = "image/image3.jpg";
    public static final String IMAGE_GIF = "image/image4.gif";

    /**
     * To fix JSON LocalDateTime mapping issue
     *
     * @return Gson
     */
    public static Gson getFullyFledgedGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                (json, type, jsonDeserializationContext) -> {
                    try{
                        return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"));
                    } catch (DateTimeParseException e){
                        return null;
                    }
                }).create();
    }

    /**
     * Load image to given type
     *
     * @param type
     * @return
     * @throws Exception
     */
    public static byte[] getImage(String type) throws Exception {
        byte[] image;

        switch (type) {
            case "PNG":
                Resource resourcePNG = new ClassPathResource(TestConstants.IMAGE_PNG);
                image = Files.readAllBytes(Path.of(resourcePNG.getURI())); break;
            case "JPEG":
                Resource resourceJPEG = new ClassPathResource(TestConstants.IMAGE_JPEG);
                image = Files.readAllBytes(Path.of(resourceJPEG.getURI())); break;
            case "JPG":
                Resource resourceJPG = new ClassPathResource(TestConstants.IMAGE_JPG);
                image = Files.readAllBytes(Path.of(resourceJPG.getURI())); break;
            default:
                Resource resourceGIF = new ClassPathResource(TestConstants.IMAGE_GIF);
                image = Files.readAllBytes(Path.of(resourceGIF.getURI())); break;
        }

        return image;
    }

    /**
     * Represent models
     *
     * @return model map
     * @throws Exception
     */
    public static Map<String, Model> loadModels() throws Exception {
        Map<String, Model> models = new ConcurrentHashMap<>();
        models.put("Lightweight", new Model(1L, "Lightweight"));
        models.put("Middleweight", new Model(2L, "Middleweight"));
        models.put("Cruiserweight", new Model(3L, "Cruiserweight"));
        models.put("Heavyweight", new Model(4L, "Heavyweight"));
        return models;
    }

    /**
     * Represent drone state
     *
     * @return state map
     * @throws Exception
     */
    public static Map<String, State> loadStatus() throws Exception {
        Map<String, State> status = new ConcurrentHashMap<>();
        status.put("IDLE", new State(1L, "IDLE"));
        status.put("LOADING", new State(2L, "LOADING"));
        status.put("LOADED", new State(3L, "LOADED"));
        status.put("DELIVERING", new State(4L, "DELIVERING"));
        status.put("DELIVERED", new State(5L, "DELIVERED"));
        status.put("RETURNING", new State(6L, "RETURNING"));
        return status;
    }
}
