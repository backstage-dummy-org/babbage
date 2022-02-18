package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

@Api
public class Health {

    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
