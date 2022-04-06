package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.template.TemplateService;
import com.github.onsdigital.babbage.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

/**
 * Created by bren on 28/05/15.
 * <p>
 * Serves rendered html output
 */
public class PageRequestHandler extends BaseRequestHandler {

    private static final String REQUEST_TYPE = "/";
    private static final String PDF = "pdf";
    private static final String PDF_STYLE = "pdf_style";
    private static final String ENABLE_COVID19_FEATURE = "EnableCovid19Feature";
    private static final String ENABLE_CENSUS_BANNER = "EnableCensusBanner";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws IOException, ContentReadException {
        return getPage(uri, request);
    }

    public static BabbageResponse getPage(String uri, HttpServletRequest request) throws ContentReadException, IOException {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri);
        try (InputStream dataStream = contentResponse.getDataStream()) {
            LinkedHashMap<String, Object> additionalData = new LinkedHashMap<>();
            if (RequestUtil.getQueryParameters(request).containsKey(PDF)) {
                additionalData.put(PDF_STYLE, true);
            }
            additionalData.put(ENABLE_COVID19_FEATURE, appConfig().handlebars().isEnableCovid19Feature());
            additionalData.put(ENABLE_CENSUS_BANNER, appConfig().handlebars().isEnableCensusBanner());
            String html = TemplateService.getInstance().renderContent(dataStream, additionalData);
            return new BabbageContentBasedStringResponse(contentResponse, html, TEXT_HTML);
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
