package com.github.onsdigital.babbage.content.client;

/**
 * Created by bren on 23/07/15.
 *
 * Content read errors when reading from content sevice fails.
 *
 * Babbage, maps any content read exceptions into appropriate http status codes
 */
public class ContentClientException extends Exception {

    //http status code to be sent to the client
    private final int statusCode;

    ContentClientException(final int statusCode, final String s) {
        this(statusCode, s, null);
    }

    ContentClientException(final int statusCode, final String s, Throwable cause) {
        super(s, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }


}

