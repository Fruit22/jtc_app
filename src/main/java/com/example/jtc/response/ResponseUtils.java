package com.example.jtc.response;

import javax.json.Json;

public final class ResponseUtils {

    private final static String success = Json.createObjectBuilder().add("Status", "Success").build().toString();
    private final static String error = Json.createObjectBuilder().add("Status", "Error").build().toString();

    private ResponseUtils() {}

    public static String createSuccessfulRs() {
        return success;
    }

    public static String createErrorRs() {
        return error;
    }
}
