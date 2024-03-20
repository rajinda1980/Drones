package com.musala.drones.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Store and manage test related constant values
 *
 * @author Rajinda
 * @version 1.0
 * @since 19/03/2024
 */
@NoArgsConstructor
public final class TestConstants {

    public static final String LOCALHOST = "http://localhost:";
    public static final String REGISTER_DRONE_URL = "/v1/api/drone/register";
    public static final String CHARSET_FOR_FILE_TRANSFORM = "UTF-8";

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
}
