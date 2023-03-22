package com.github.onsdigital.babbage.content.client;

import com.github.onsdigital.babbage.util.http.CacheHttpClient;
import com.github.onsdigital.babbage.util.http.ClientConfiguration;
import com.google.common.base.Verify;
import com.google.common.net.MediaType;
import junit.framework.TestCase;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.AtLeast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.content.client.ContentClientCache.createConfiguration;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ContentClientCacheTest extends TestCase {
@Mock
private ContentClientCache mockClient;

    private static final String TAXONOMY_ENDPOINT = "/taxonomy";
    private static final String NAVIGATION_ENDPOINT = "/navigation";

    @Mock
    private CacheHttpClient mockHpptClient;

    @Mock
    private ClientConfiguration mockConfiguration;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockHpptClient = new CacheHttpClient(appConfig().contentAPI().serverURL(), createConfiguration());

    }
//    @Test
//    public void testGetInstance() {
//        ContentClientCache instance = mockClient.getInstance();
//        verify(mockClient, times(1)).getInstance();
//    }

    @Test
    public void testGetTaxonomy_EmptyParams() throws ContentReadException {
        Map<String, String[]> queryParameters = new HashMap<String, String[]>();
        try {
            ContentResponse response = mockClient.getTaxonomy(queryParameters);
            verify(mockClient, atLeastOnce()).getTaxonomy(queryParameters);
        } catch (ContentReadException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetTaxonomy_nullParams() throws ContentReadException {
        Map<String, String[]> queryParameters = null;
        try {
            ContentResponse response = mockClient.getTaxonomy(queryParameters);
            verify(mockClient, atLeastOnce()).getTaxonomy(queryParameters);
        } catch (ContentReadException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetNavigation_EmptyParams() {
        Map<String, String[]> queryParameters = new HashMap<String, String[]>();
        try {
            ContentResponse response = mockClient.getNavigation(queryParameters);
            verify(mockClient, atLeastOnce()).getNavigation(queryParameters);
        } catch (ContentReadException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testGetNavigation_nullParams() throws ContentReadException {
        Map<String, String[]> queryParameters = null;
        try {
            ContentResponse response = mockClient.getNavigation(queryParameters);
            verify(mockClient, atLeastOnce()).getNavigation(queryParameters);
        } catch (ContentReadException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetNavigation_cy() throws ContentReadException {
//        ContentResponse response = new ContentResponse();
        Map<String, String[]> queryParameters = new HashMap<String, String[]>();
        String[] param  = {"2"};
        queryParameters.put("depth",param);

        List<NameValuePair> returnParams = new ArrayList<NameValuePair>();
        returnParams.add(new BasicNameValuePair("depth", "2"));
        returnParams.add(new BasicNameValuePair("lang", "cy"));
        when(mockClient.getParameters(queryParameters)).thenReturn(returnParams);
//        when(mockClient.sendGet(anyString(),returnParams)).thenReturn(response);



        try {
            ContentResponse response = mockClient.getNavigation(queryParameters);
            verify(mockClient, atLeastOnce()).getNavigation(queryParameters);
        } catch (ContentReadException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSendGet_empty() throws ContentReadException {
        ContentResponse response = mock(ContentResponse.class);

        List<NameValuePair> returnParams = new ArrayList<NameValuePair>();
        returnParams.add(new BasicNameValuePair("depth", "2"));
        returnParams.add(new BasicNameValuePair("lang", "cy"));

        when(mockClient.sendGet(anyString(),anyListOf(NameValuePair.class))).thenReturn(response);

        response = mockClient.sendGet(NAVIGATION_ENDPOINT,returnParams);
        verify(mockClient,atLeastOnce()).sendGet(anyString(),anyList());
        assertNotNull(response);

    }

    @Test
    public void testSendGet_null() throws ContentReadException {
        ContentResponse response = null;

        List<NameValuePair> returnParams = new ArrayList<NameValuePair>();
        returnParams.add(new BasicNameValuePair("depth", "2"));
        returnParams.add(new BasicNameValuePair("lang", "cy"));
        when(mockClient.sendGet(anyString(),anyListOf(NameValuePair.class))).thenReturn(response);

        response = mockClient.sendGet(NAVIGATION_ENDPOINT,returnParams);
        verify(mockClient,atLeastOnce()).sendGet(anyString(),anyList());
        assertNull(response);

    }


}