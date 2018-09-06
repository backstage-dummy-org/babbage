package com.github.onsdigital.babbage.search.external.requests.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.search.external.SearchClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;

import java.util.concurrent.Callable;

public abstract class AbstractSearchRequest<T> implements Callable<T> {

    protected static final String HOST = Configuration.SEARCH_SERVICE.getExternalSearchAddress();

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected SearchClient searchClient;

    private final Class<T> returnClass;

    public AbstractSearchRequest(Class<T> returnClass) {
        this.returnClass = returnClass;
    }

    /**
     * Abstract method for building/returning the target URI for HTTP requests
     * @return
     */
    public abstract URIBuilder targetUri();

    private SearchClient getSearchClient() throws Exception {
        if (searchClient == null) {
            searchClient = SearchClient.getInstance();
        }
        return searchClient;
    }

    /**
     * Builds a simple HTTP GET request with the target URI
     * @return
     * @throws Exception
     */
    protected Request get() throws Exception {
        searchClient = this.getSearchClient();
        return searchClient.get(this.targetUri().toString());
    }

    /**
     * Builds a HTTP POST request with mime-type application/json
     * @return
     */
    protected Request post() throws Exception {
        searchClient = this.getSearchClient();
        return searchClient.post(this.targetUri().toString())
                .header(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    }

    /**
     * Abstract method for executing requests
     * @return
     * @throws Exception
     */
    protected abstract ContentResponse getContentResponse() throws Exception;

    public String getContentResponseAsString() throws Exception {
        return this.getContentResponse().getContentAsString();
    }

    @Override
    public T call() throws Exception {
        String response = this.getContentResponseAsString();
        return MAPPER.readValue(response, this.returnClass);
    }

}
