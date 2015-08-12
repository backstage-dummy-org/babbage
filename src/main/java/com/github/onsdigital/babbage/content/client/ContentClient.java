package com.github.onsdigital.babbage.content.client;

import com.github.onsdigital.babbage.util.ThreadContext;
import com.github.onsdigital.babbage.util.http.PooledHttpClient;
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

import static com.github.onsdigital.configuration.Configuration.CONTENT_SERVER.*;

/**
 * Created by bren on 23/07/15.
 * <p>
 * Content service reads content from external server over http.
 * <p>
 * Using Apache http client with connection pooling.
 */
//TODO: Get http client use cache headers returned from content service
public class ContentClient {

    private static final String TOKEN_HEADER = "X-Florence-Token";


    private static PooledHttpClient client;
    private static ContentClient instance;

    //singleton
    private ContentClient() {
    }

    public static ContentClient getInstance() {
        if (instance == null) {
            synchronized (ContentClient.class) {
                if (instance == null) {
                    instance = new ContentClient();
                    System.out.println("Initializing content service http client");
                    client = new PooledHttpClient(getServerUrl());
                    client.getConfiguration().setMaxConnection(getMaxContentServiceConnection());
                }
            }
        }
        return instance;
    }


    /**
     * Serves requested content data, stream should be closed after use or fully consumed, fully consuming the stream will close the stream automatically
     * Content  client forwards any requests headers and cookies to content service using saved ThreadContext
     * <p>
     * Any request headers like authentication tokens or collection ids should be saved to ThreadContext before calling content service
     *
     * @param uri uri for requested content.
     *            e.g./economy/inflationandpriceindices for content data requests
     *            e.g./economy/inflationandpriceindices/somecontent.xls  ( no data.json after the uri )
     * @return Json for requested content
     * @throws ContentReadException If content read fails due content service error status is set to whatever error is sent back from content service,
     *                                all other IO Exceptions are rethrown with HTTP status 500
     */
    public ContentStream getContentStream(String uri) throws ContentReadException, IOException {
        return getContentStream(uri, null);
    }

    /**
     * Serves requested content data, stream should be closed after use or fully consumed, fully consuming the stream will close the stream automatically
     * Content  client forwards any requests headers and cookies to content service using saved ThreadContext
     * <p>
     * Any request headers like authentication tokens or collection ids should be saved to ThreadContext before calling content client
     *
     * @param uri             uri for requested content.
     *                        e.g./economy/inflationandpriceindices for content data requests
     *                        e.g./economy/inflationandpriceindices/somecontent.xls  ( no data.json after the uri )
     * @param queryParameters GET parameters that needs to be passed to content service (e.g. filter parameters)
     * @return Json for requested content
     * @throws ContentReadException If content read fails due content service error status is set to whatever error is sent back from content service,
     *                                all other IO Exceptions are rethrown with HTTP status 500
     */
    public ContentStream getContentStream(String uri, Map<String, String[]> queryParameters) throws ContentReadException {
        System.out.println("getContentStream(): Reading content from content server, uri:" + uri);
        return sendGet(getDataPath(), getParameters(uri, queryParameters));
    }

    public ContentStream getChildren(String uri, Map<String, String[]> queryParameters) throws ContentReadException {
        System.out.println("getChildren(): Reading child tree, uri: " + uri);
        return sendGet(getChildContentPath(), getParameters(uri, queryParameters));
    }

    public ContentStream getParents(String uri) throws ContentReadException {
        return sendGet(getParentsPath(), getParameters(uri,null));
    }

    public ContentStream getParents(String uri, Map<String, String[]> queryParameters) throws ContentReadException {
        System.out.println("getParents(): Reading parents, uri:" + uri);
        return sendGet(getParentsPath(), getParameters(uri,queryParameters));
    }

    private ContentStream sendGet(String path, List<NameValuePair> getParameters) throws ContentReadException {
        CloseableHttpResponse response = null;
        try {
            return new ContentStream(client.sendGet(path, getHeaders(),getParameters ));
        } catch (HttpResponseException e) {
            IOUtils.closeQuietly(response);
            throw wrapException(e);
        } catch (IOException e) {
            IOUtils.closeQuietly(response);
            throw wrapException(e);
        }
    }

    public ContentStream getBreadCrumb(String uri) throws ContentReadException {
        System.out.println("getBreadcrumb(): Reading breadcrumb from content server:" + uri);
        ArrayList<NameValuePair> queryParameters = new ArrayList<>();
        queryParameters.add(new BasicNameValuePair("uri", uri));

        CloseableHttpResponse response = null;
        try {
            response = client.sendGet(getParentsPath(), getHeaders(), queryParameters);
            return new ContentStream(response);
        } catch (HttpResponseException e) {
            IOUtils.closeQuietly(response);
            throw wrapException(e);
        } catch (IOException e) {
            IOUtils.closeQuietly(response);
            throw wrapException(e);
        }
    }

    private List<NameValuePair> getParameters(String uri, Map<String, String[]> parametes) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        uri = StringUtils.isEmpty(uri) ? "/" : uri;

        //uris are requested as get parameter from content service
        nameValuePairs.add(new BasicNameValuePair("uri", uri));
        if (parametes != null) {
            for (Iterator<Map.Entry<String, String[]>> iterator = parametes.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String[]> entry = iterator.next();
                String[] values = entry.getValue();
                if (ArrayUtils.isEmpty(values)) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), null));
                    continue;
                }
                for (int i = 0; i < values.length; i++) {
                    String s = entry.getValue()[i];
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), values[i]));
                }
            }
        }
        return nameValuePairs;
    }


    private ContentReadException wrapException(HttpResponseException e) {
        return new ContentReadException(e.getStatusCode(), e.getMessage());
    }

    private ContentReadException wrapException(IOException e) {
        return new ContentReadException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal Server Error", e);
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
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(TOKEN_HEADER, cookies.get("access_token"));
        return headers;
        }
        return null;
    }

    private String getDataPath() {
        String collectionId = getCollectionId();
        if (collectionId == null) {
            return getDataEndpoint();
        } else {
            return getDataPath() + "/" + collectionId;
        }
    }

    private String getChildContentPath() {
        String collectionId = getCollectionId();
        if (collectionId == null) {
            return getChildrenEndpoint();
        } else {
            return getChildrenEndpoint() + "/" + collectionId;
        }
    }


    private String getParentsPath() {
        String collectionId = getCollectionId();
        if (collectionId == null) {
            return getParentsEndpoint();
        } else {
            return getParentsEndpoint() + "/" + collectionId;
        }
    }

}