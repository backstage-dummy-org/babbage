package com.github.onsdigital.api.util;

import com.github.onsdigital.content.page.error.Error404;
import com.github.onsdigital.content.page.error.Error500;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.error.ResourceNotFoundException;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bren on 28/05/15.
 * <p/>
 * Handles exceptions and returns appropriate response to the client. It is possible to take actions for specific error types
 */
public class ApiErrorHandler {

    private static String HTML_CONTENT_TYPE = "text/html";

    public static void handle(Throwable e, HttpServletResponse response) throws IOException {

        logError(e);
        Map<String, String> errorResponse = new HashMap<String, String>();

        //TODO: Tidy up exception management
        if (e instanceof ResourceNotFoundException || e instanceof ContentNotFoundException) {
            Error404 error404 = new Error404();
            error404.setNavigation(NavigationUtil.getNavigation());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType(HTML_CONTENT_TYPE);
            IOUtils.copy(new StringReader(TemplateService.getInstance().renderPage(error404)), response.getOutputStream());
//            errorResponse.put("message", "Resource you are looking for is not available");
//            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_NOT_FOUND));
        } else if (e instanceof IllegalArgumentException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
        } else {
            Error500 error500 = new Error500();
            error500.setNavigation(NavigationUtil.getNavigation());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(HTML_CONTENT_TYPE);
            IOUtils.copy(new StringReader(TemplateService.getInstance().renderPage(error500)), response.getOutputStream());
//            errorResponse.put("status", String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
//            errorResponse.put("message", "Internal Server Error Occurred!");
        }
    }

    private static void logError(Throwable e) {
        e.printStackTrace();
    }
}