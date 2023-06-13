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
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Date;
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

    @Mock
    private CloseableHttpResponse closeableHttpResponseMock;

    @Mock
    private Metrics metricsMock;

    @Mock
    private HttpEntity httpEntityMock;

    private String uriStr = "economy/environmentalaccounts/articles/environmentaltaxes/2015-06-01";
    private int maxAgeSeconds = 900;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        contentClient = ContentClient.getInstance();

        TestsUtil.setPrivateField(contentClient, "publishingManager", publishingManagerMock);
        TestsUtil.setPrivateField(contentClient, "metrics", metricsMock);
        TestsUtil.setPrivateStaticField(contentClient, "client", clientMock);
        TestsUtil.setPrivateStaticField(contentClient, "cacheEnabled", true);
        TestsUtil.setPrivateStaticField(contentClient, "maxAge", maxAgeSeconds);
    }

    @Test
    public void testPublishDateNotPresentIsIncremented() throws Exception {
        //Given
        PublishInfo nextPublish = new PublishInfo(uriStr, null, null, null);
        when(publishingManagerMock.getNextPublishInfo(uriStr)).thenReturn(nextPublish);

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("uri", "/" + uriStr));
        Header[] headers = {
                new BasicHeader("Content-type", "application/json")
        };
        when(httpEntityMock.getContentType()).thenReturn(headers[0]);
        when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntityMock);

        List<NameValuePair> parameters2 = new ArrayList<>();
        parameters2.add(new BasicNameValuePair("lang", null));
        parameters2.add(new BasicNameValuePair("uri", uriStr));
        when(clientMock.sendGet("/data", null, parameters2)).thenReturn(closeableHttpResponseMock);

        //When
        contentClient.getContent(uriStr);

        //Then
        verify(metricsMock, times(1)).incPublishDateNotPresent();
    }

    @Test
    public void testPublishDateInRangeIsIncremented() throws Exception {
        //Given
        int secondsUntilPublish = 600; //This needs to be less than maxAge to be in range
        Date publishDate = DateTime.now().plusSeconds(secondsUntilPublish).toDate();
        PublishInfo nextPublish = new PublishInfo(uriStr, null, publishDate, null);
        when(publishingManagerMock.getNextPublishInfo(uriStr)).thenReturn(nextPublish);

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("uri", "/" + uriStr));
        Header[] headers = {
                new BasicHeader("Content-type", "application/json")
        };
        when(httpEntityMock.getContentType()).thenReturn(headers[0]);
        when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntityMock);

        List<NameValuePair> parameters2 = new ArrayList<>();
        parameters2.add(new BasicNameValuePair("lang", null));
        parameters2.add(new BasicNameValuePair("uri", uriStr));
        when(clientMock.sendGet("/data", null, parameters2)).thenReturn(closeableHttpResponseMock);

        //When
        contentClient.getContent(uriStr);

        //Then
        verify(metricsMock, times(1)).incPublishDateInRange();
        assert(maxAgeSeconds > secondsUntilPublish);
    }

    @Test
    public void testPublishDateTooFarInFutureIsIncremented() throws Exception {
        //Given
        int secondsUntilPublish = 1000; //This needs to be more than maxAge to be too far in the future
        Date publishDate = DateTime.now().plusSeconds(secondsUntilPublish).toDate();
        PublishInfo nextPublish = new PublishInfo(uriStr, null, publishDate, null);
        when(publishingManagerMock.getNextPublishInfo(uriStr)).thenReturn(nextPublish);

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("uri", "/" + uriStr));
        Header[] headers = {
                new BasicHeader("Content-type", "application/json")
        };
        when(httpEntityMock.getContentType()).thenReturn(headers[0]);
        when(closeableHttpResponseMock.getEntity()).thenReturn(httpEntityMock);

        List<NameValuePair> parameters2 = new ArrayList<>();
        parameters2.add(new BasicNameValuePair("lang", null));
        parameters2.add(new BasicNameValuePair("uri", uriStr));
        when(clientMock.sendGet("/data", null, parameters2)).thenReturn(closeableHttpResponseMock);

        //When
        contentClient.getContent(uriStr);

        //Then
        assert(maxAgeSeconds < secondsUntilPublish);
        verify(metricsMock, times(1)).incPublishDateTooFarInFuture();
    }
}
