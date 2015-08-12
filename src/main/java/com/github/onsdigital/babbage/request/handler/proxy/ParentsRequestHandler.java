package com.github.onsdigital.babbage.request.handler.proxy;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.request.response.BabbageResponse;
import com.github.onsdigital.request.response.BabbageStringResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.github.onsdigital.babbage.util.RequestUtil.getQueryParameters;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class ParentsRequestHandler implements RequestHandler {

    private static final String REQUEST_TYPE = "parents";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws IOException, ContentNotFoundException, ContentReadException {
        return new BabbageStringResponse(ContentClient.getInstance().getParents(uri, getQueryParameters(request)).getAsString());
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
