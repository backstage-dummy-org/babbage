package com.github.onsdigital.request.handler;

import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests at the endpoint /table.
 * Renders a chart and associated content in an isolated page.
 */
public class TableRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "table";
    public static final String CONTENT_TYPE = "text/html";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        return new BabbageStringResponse("", CONTENT_TYPE);
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
