package com.github.onsdigital.babbage.content.client;

import com.github.onsdigital.babbage.metrics.Metrics;
import com.github.onsdigital.babbage.publishing.PublishingManager;
import com.github.onsdigital.babbage.publishing.model.PublishInfo;
import com.github.onsdigital.babbage.util.TestsUtil;
import com.github.onsdigital.babbage.util.http.PooledHttpClient;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class ContentClientTest {

    //  Test target.

    @InjectMocks
    @Spy
    private ContentClient contentClient;

    @Mock
    private PublishingManager publishingManagerMock;

    @Mock
    private static PooledHttpClient clientMock;

//    @Mock ContentResponse contentResponseMock;
//
    @Mock
    private CloseableHttpResponse closeableHttpResponseMock;

    @Mock
    private Metrics metricsMock;

//    @Mock
//    private ContentType contentTypeMock;

//    @Mock
//    private Header headerMock;

    @Mock
    private HttpEntity httpEntityMock;

    private String uriStr = "economy/environmentalaccounts/articles/environmentaltaxes/2015-06-01";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        contentClient = ContentClient.getInstance();

        TestsUtil.setPrivateField(contentClient, "publishingManager", publishingManagerMock);
        TestsUtil.setPrivateField(contentClient, "metrics", metricsMock);
        TestsUtil.setPrivateStaticField(contentClient, "client", clientMock);
        TestsUtil.setPrivateStaticField(contentClient, "cacheEnabled", true);
    }

    @Test
    public void testPublishDateNotPresentIsIncremented() throws Exception {
        //Given
        PublishInfo nextPublish = new PublishInfo(uriStr, null, null, null);
        when(publishingManagerMock.getNextPublishInfo(uriStr)).thenReturn(nextPublish);

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("uri", "/economy/environmentalaccounts/articles/environmentaltaxes/2015-06-01"));
        Header[] headers = {
                new BasicHeader("Content-type", "application/json")
        };
        when(httpEntityMock.getContentType()).thenReturn(headers[0]);
        when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntityMock);

        List<NameValuePair> parameters2 = new ArrayList<>();
        parameters2.add(new BasicNameValuePair("lang", null));
        parameters2.add(new BasicNameValuePair("uri", "economy/environmentalaccounts/articles/environmentaltaxes/2015-06-01"));
        when(clientMock.sendGet("/data", null, parameters2)).thenReturn(closeableHttpResponseMock);

        //When
        contentClient.getContent(uriStr);

        //Then
        verify(metricsMock, times(1)).incPublishDateNotPresent();
    }
}
