package com.github.onsdigital.babbage.response;

import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.response.util.CacheControlHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class BabbageContentBasedBinaryResponse extends BabbageBinaryResponse {

    private ContentResponse contentResponse;

    public BabbageContentBasedBinaryResponse(ContentResponse contentResponse, InputStream data, String mimeType) throws IOException {
        super(data, mimeType);
        this.contentResponse = contentResponse;
    }

    protected void setCacheHeaders(HttpServletRequest request, HttpServletResponse response) {
        CacheControlHelper.setCacheHeaders(request, response, contentResponse.getHash(), contentResponse.getMaxAge());
    }

}