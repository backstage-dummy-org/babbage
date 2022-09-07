package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.LegacyPDFException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.babbage.util.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.warn;


/**
 * Request handler that retrieves a previously generated pdf file
 */
public class GetPDFRequestHandler extends BaseRequestHandler {

    private static final String REQUEST_TYPE = "pdf";

    @Override
    public BabbageResponse get(String requestedUri, HttpServletRequest request) throws Exception {
        try {
            return getPreGeneratedPDF(requestedUri, (String) ThreadContext.getData(RequestUtil.LANG_KEY));
        } catch (ContentReadException | ResourceNotFoundException e) {
            error().exception(e).data("uri", requestedUri).log("pre-rendered PDF not found, throwing Legacy PDF error");
            throw new LegacyPDFException();
        }
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    private static BabbageResponse getPreGeneratedPDF(String requestedUri, String language) throws ContentReadException, IOException {
        ContentResponse contentResponse = null;
        if ("cy".equals(language)) {
            try {
                contentResponse = ContentClient.getInstance().getResource(requestedUri + "/page_cy.pdf");
            } catch(ResourceNotFoundException e) {
                warn().exception(e).data("uri", requestedUri).log("pre-rendered Welsh PDF not found, using English version");
                // There is no Welsh PDF, we'll serve the English version
                contentResponse = null;
            }
        }

        if (contentResponse == null) {
            // Use the English version
            contentResponse = ContentClient.getInstance().getResource(requestedUri + "/page.pdf");
        }
        BabbageContentBasedBinaryResponse response = new BabbageContentBasedBinaryResponse(contentResponse, contentResponse.getDataStream(), contentResponse.getMimeType());
        String contentDispositionHeader = "attachment; ";
        contentDispositionHeader += contentResponse.getName() == null ? "" : "filename=\"" + getTitle(requestedUri) + "\"";
        response.addHeader("Content-Disposition", contentDispositionHeader);
        return response;
    }

    public static String getTitle(String uri) throws IOException, ContentReadException {
        ContentResponse contentResponse = ContentClient.getInstance().getContent(uri, filter(ContentFilter.DESCRIPTION));
        Map<String, Object> stringObjectMap = JsonUtil.toMap(contentResponse.getDataStream());

        Map<String, Object> descriptionMap = (Map<String, Object>) stringObjectMap.get("description");

        String title = (String) descriptionMap.get("title");
        String edition = (String) descriptionMap.get("edition");

        if (StringUtils.isNotEmpty(edition)) {
            title += " " + edition;
        }

        title += ".pdf";

        return title;
    }
}
