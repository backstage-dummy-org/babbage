package com.github.onsdigital.babbage.util.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

/**
 * Created by ann on 22/11/22
 * <p/>
 * http client to a single host with connection pool and cache functionality.
 */
public class CacheHttpClient extends PooledHttpClient {
    protected final CloseableHttpClient httpClient;

    public CacheHttpClient(String host, ClientConfiguration configuration) {
        super(host, configuration);

        CachingHttpClientBuilder cacheClientBuilder = CachingHttpClients.custom();
        CacheConfig cacheConfig = CacheConfig.custom()
                .setMaxCacheEntries(3000)
                .setMaxObjectSize(50000) // 10MB
                .build();

        this.httpClient = cacheClientBuilder
                .setCacheConfig(cacheConfig)
                .build();
    }


    public CloseableHttpResponse sendGet(String path, Map<String, String> headers, List<NameValuePair> queryParameters) throws IOException {
        URI uri = buildGetUri(path, queryParameters);
        HttpGet request = new HttpGet(uri);
        addHeaders(headers, request);

        CloseableHttpResponse response = executeRequest(request);
        return validateResponse(response);
    }

    private CloseableHttpResponse executeRequest(HttpRequestBase request) throws IOException {
        HttpCacheContext context = HttpCacheContext.create();

        info().beginHTTP(request).log("CacheHttpClient executing request");

        CloseableHttpResponse response = httpClient.execute(request,context);

        try {
            CacheResponseStatus responseStatus = context.getCacheResponseStatus();

            switch (responseStatus) {
                case CACHE_HIT:
                    info().endHTTP(request, response).log(responseStatus + " A response was generated from the cache with no requests sent upstream" );
                    break;

                case CACHE_MODULE_RESPONSE:
                    info().endHTTP(request, response).log(responseStatus + " The response was generated directly by the caching module" );
                    break;

                case CACHE_MISS:
                    info().endHTTP(request, response).log(responseStatus + " The response came from an upstream server" );
                    break;

                case VALIDATED:
                    info().endHTTP(request, response).log(responseStatus + " The response was generated from the cache after validating the entry with the origin server" );
                    break;

            }
        } finally {
            info().endHTTP(request, response).log(Arrays.toString(response.getHeaders("Cache-Control")) + " the header cache-control" );
        }

        info().endHTTP(request, response).log("CacheHttpClient execute request completed");
        return response;
    }

    private URI buildPath(String path) {
        URIBuilder uriBuilder = newUriBuilder(path);
        try {
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid uri! " + host + path);
        }
    }

    private URIBuilder newUriBuilder(String path) {
        URIBuilder uriBuilder = new URIBuilder(host);
        String fullPath = "/" + path;
        String prefix = uriBuilder.getPath();
        if (prefix != null) {
            fullPath = prefix + fullPath;
        }
        uriBuilder.setPath(fullPath.replaceAll("//+", "/"));

        return uriBuilder;
    }

    private URI buildGetUri(String path, List<NameValuePair> queryParameters) {
        try {
            URIBuilder uriBuilder = newUriBuilder(path);
            if (queryParameters != null) {
                uriBuilder.setParameters(queryParameters);
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid uri! " + host + path);
        }
    }


    private void addHeaders(Map<String, String> headers, HttpRequestBase request) {
        if (headers != null) {
            Iterator<Map.Entry<String, String>> headerIterator = headers.entrySet().iterator();
            while (headerIterator.hasNext()) {
                Map.Entry<String, String> next = headerIterator.next();
                request.addHeader(next.getKey(), next.getValue());
            }

        }
    }

    private CloseableHttpResponse validateResponse(CloseableHttpResponse response)
            throws ClientProtocolException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();

        if (statusLine.getStatusCode() > 302) {
            String errorMessage = getErrorMessage(entity);
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    errorMessage == null ? statusLine.getReasonPhrase() : errorMessage);
        }

        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }

        return response;
    }

    private String getErrorMessage(HttpEntity entity) {
        try {
            String s = EntityUtils.toString(entity);
            return s;
        } catch (Exception e) {
            error().exception(e).log("Failed reading content service:");
        }
        return null;
    }




}
