package com.github.onsdigital.babbage.request.handler;

import static com.github.onsdigital.babbage.content.client.ContentClient.filter;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentFilter;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.util.json.JsonUtil;

public abstract class PDFRequestHeandler extends BaseRequestHandler {

    protected String getTitle(String uri) throws IOException, ContentReadException {
        ContentResponse contentResponse = getContent(uri, filter(ContentFilter.DESCRIPTION));
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

    protected ContentResponse getContent(String uri, Map<String, String[]> queryParameters) throws ContentReadException {
        return ContentClient.getInstance().getContent(uri, queryParameters);
    }
}
