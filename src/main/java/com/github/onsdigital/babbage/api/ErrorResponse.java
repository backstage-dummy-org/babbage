package com.github.onsdigital.babbage.api;

/**
 * Created by bren on 10/08/15.
 *
 * Error respons is used to return error message in json format when data end point is requested
 *
 */
public class ErrorResponse {

    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

}
