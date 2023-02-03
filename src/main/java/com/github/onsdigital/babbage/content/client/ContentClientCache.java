package com.github.onsdigital.babbage.content.client;


import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.publishing.model.PublishInfo;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.babbage.util.http.CacheHttpClient;
import com.github.onsdigital.babbage.util.http.ClientConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.error;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

/**
 * Created by Ann on 11/22.
 * <p/>
 * Content service reads content from external server over http.
 * <p/>
 * Using Apache http client with connection pooling.
 */
public class ContentClientCache {

    private static final String TOKEN_HEADER = "X-Florence-Token";

    private static CacheHttpClient client;
    private static ContentClientCache instance;

    private static final String DATA_ENDPOINT = "/data";
    private static final String TAXONOMY_ENDPOINT = "/taxonomy";
    private static final String NAVIGATION_ENDPOINT = "/navigation";

    public static ContentClientCache getInstance() {
        if (instance == null) {
            synchronized (ContentClientCache.class) {
                if (instance == null) {
                    info().log("initialising ContentClientCache instance");
                    instance = new ContentClientCache();
                    info().log("initialising CacheHttpClient for ContentClientCache instance");
                    client = new CacheHttpClient(appConfig().contentAPI().serverURL(), createConfiguration());
                }
            }
        }
        return instance;
    }

    public ContentResponse getContent(String uri, Map<String, String[]> queryParameters) throws ContentReadException {
        return resolveMaxAge(uri, sendGet(getPath(DATA_ENDPOINT), addUri(uri, getParameters(queryParameters))));
    }

    private List<NameValuePair> addUri(String uri, List<NameValuePair> parameters) {
        if (parameters == null) {
            return null;
        }
        uri = StringUtils.isEmpty(uri) ? "/" : uri;
        //uris are requested as get parameter from content service
        parameters.add(new BasicNameValuePair("uri", uri));
        return parameters;
    }


    private static ClientConfiguration createConfiguration() {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxTotalConnection(appConfig().contentAPI().maxConnections());
        configuration.setDisableRedirectHandling(true);
        return configuration;
    }

    public ContentResponse getTaxonomy(Map<String, String[]> queryParameters) throws ContentReadException {
            System.out.println("ContentClientCache.getTaxonomy" + queryParameters);
            return sendGet(getPath(TAXONOMY_ENDPOINT), getParameters(queryParameters));
    }

    public ContentResponse getTaxonomy() throws ContentReadException {
            return sendGet(getPath(TAXONOMY_ENDPOINT), null);
    }

    private ContentResponse resolveMaxAge(String uri, ContentResponse response) {
        if (!appConfig().babbage().isCacheEnabled()) {
            return response;
        }

        try {
            PublishInfo nextPublish = PublishingManager.getInstance().getNextPublishInfo(uri);
            Date nextPublishDate = nextPublish == null ? null : nextPublish.getPublishDate();
            int maxAge = appConfig().babbage().getDefaultContentCacheTime();
            Integer timeToExpire = null;
            if (nextPublishDate != null) {
                Long time = (nextPublishDate.getTime() - new Date().getTime()) / 1000;
                timeToExpire = time.intValue();
            }

            if (timeToExpire == null) {
                response.setMaxAge(maxAge);
            } else if (timeToExpire > 0) {
                response.setMaxAge(timeToExpire < maxAge ? timeToExpire : maxAge);
            } else if (timeToExpire < 0 && Math.abs(timeToExpire) > appConfig().babbage().getPublishCacheTimeout()) {
                //if publish is due but there is still a publish date record after an hour drop it
                info().data("uri", uri).log("dropping publish date record due to publish wait timeout for uri");
                PublishingManager.getInstance().dropPublishDate(nextPublish);
                return resolveMaxAge(uri, response);//resolve for next publish date if any
            }
        } catch (Exception e) {
            error().exception(e)
                    .data("uri", uri)
                    .log("managing publish date failed for uri, skipping setting cache times");
        }
        return response;
    }

    /**
     * @param path
     * @param getParameters
     * @return
     * @throws ContentReadException
     */
    private ContentResponse sendGet(String path, List<NameValuePair> getParameters) throws ContentReadException {
        CloseableHttpResponse response = null;
        try {
            System.out.println("Path " + path);
            System.out.println("Headers " + getHeaders());
            System.out.println("Parameters " + getParameters);
            return new ContentResponse(client.sendGet(path, getHeaders(), getParameters));
        } catch (HttpResponseException e) {
            //noinspection deprecation
            IOUtils.closeQuietly(response);
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                info().data("uri", path).log("ContentClientCache requested uri not found");
                throw new ResourceNotFoundException(e.getMessage());
            }

            throw wrapException( e);

        } catch (IOException e) {
            //noinspection deprecation
            IOUtils.closeQuietly(response);
            throw wrapException( e);
        }
    }

    private List<NameValuePair> getParameters(Map<String, String[]> parameters) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("lang", (String) ThreadContext.getData(RequestUtil.LANG_KEY)));
        nameValuePairs.addAll(toNameValuePair(parameters));
        return nameValuePairs;
    }

    private List<NameValuePair> toNameValuePair(Map<String, String[]> parameters) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (parameters != null) for (Entry<String, String[]> entry : parameters.entrySet()) {
            String[] values = entry.getValue();
            if (ArrayUtils.isEmpty(values)) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), null));
                continue;
            }
            for (String value : values) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }
        return nameValuePairs;
    }

    private ContentReadException wrapException(HttpResponseException e) {
        error().exception(e).log("ContentClientCache request returned error");
        return new ContentReadException(e.getStatusCode(), "Failed reading from content service", e);
    }

    private ContentReadException wrapException(IOException e) {
        error().exception(e).log("ContentClientCache request returned error");
        return new ContentReadException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Failed reading from content service", e);
    }

    //Reads collection cookie saved in thread context
    private String getCollectionId() {
        Map<String, String> cookies = (Map<String, String>) ThreadContext.getData("cookies");
        if (cookies != null) {
            return cookies.get("collection");
        }
        return null;
    }

    private Map<String, String> getHeaders() {
        Map<String, String> cookies = (Map<String, String>) ThreadContext.getData("cookies");
        if (cookies != null) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(TOKEN_HEADER, cookies.get("access_token"));
            return headers;
        }
        return null;
    }

    private String getPath(String endpoint) {
        String collectionId = getCollectionId();
        if (collectionId == null) {

//             TODO
//            if ( appConfig().babbage().isNavigationEnabled() ) {
//                endpoint = "http://localhost:25300/navigation";
//            }
            return endpoint;
        } else {
            return endpoint + "/" + collectionId;
        }
    }
}



