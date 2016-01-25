package com.github.onsdigital.babbage.response.base;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.response.util.CacheControlHelper;
import org.apache.commons.lang3.CharEncoding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by bren on 08/06/15.
 *
 * A successful response for http request
 *
 */
public abstract class BabbageResponse{

    private String mimeType = APPLICATION_JSON; //Default mimetype
    private String charEncoding = CharEncoding.UTF_8;//Default encoding
    private int status = HttpServletResponse.SC_OK;//Default status
    private Long maxAge;
    private Map<String, String> header;

    public BabbageResponse(String mimeType) {
        this.mimeType = mimeType;
    }

    public BabbageResponse(String mimeType, int status) {
        this(mimeType);
        this.status = status;
    }

    public BabbageResponse(String mimeType, Long maxAge) {
        this(mimeType);
        setMaxAge(maxAge);
    }

    public BabbageResponse(String mimeType, int status, Long maxAge) {
        this(mimeType, status);
        setMaxAge(maxAge);
    }

    public BabbageResponse() { }

    public void apply(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(getStatus());
        response.setCharacterEncoding(getCharEncoding());
        response.setContentType(getMimeType());
        if (getHeader() != null) {
            Set<Map.Entry<String, String>> entries = getHeader().entrySet();
            for (Map.Entry<String, String> next : entries) {
                response.setHeader(next.getKey(), next.getValue());
            }
        }
        setCacheHeaders(request, response);
    }

    protected void setCacheHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (maxAge != null) {
            CacheControlHelper.setCacheHeaders(response, maxAge);
        }
    }


    public String getMimeType() {
        return mimeType;
    }

    public BabbageResponse setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public void addHeader(String key, String value) {
        if (header == null) {
            header = new HashMap<>();
        }
        header.put(key, value);
    }

    protected Map<String, String> getHeader() {
        return header;
    }

    public BabbageResponse setCharEncoding(String charEncoding) {
        this.charEncoding = charEncoding;
        return this;
    }

    public String getCharEncoding() {
        return charEncoding;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Long maxAge) {
        if (Configuration.GENERAL.isCacheEnabled()) {
            this.maxAge = maxAge;
        }
    }
}
