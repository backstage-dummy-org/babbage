package com.github.onsdigital.babbage.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;


import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)

public class CacheHttpClientTest {

    @Mock
    private CacheHttpClient client;



    @Test
    public void testSendGet() throws IOException, URISyntaxException {

        Map<String, String> headers1;
        headers1 = new HashMap<>();
        headers1.put("ar01", "Intro to Map");
        headers1.put("ar02", "Some article");

        List<NameValuePair> queryParameters = new ArrayList<NameValuePair>();
        queryParameters.add(new BasicNameValuePair("lang", "cy"));

        CloseableHttpResponse resp1 = mock(CloseableHttpResponse.class);
        CloseableHttpResponse resp2 = mock(CloseableHttpResponse.class);
        String path1 = "/taxonomy";
        when(client.sendGet(path1,headers1,queryParameters)).thenReturn(resp1);
        CloseableHttpResponse returnedResp1 = client.sendGet(path1,headers1,queryParameters);
        CloseableHttpResponse returnedResp2 = client.sendGet(path1,headers1,queryParameters);

        verify(client, times(2)).sendGet(path1,headers1,queryParameters);

        HttpEntity entity = returnedResp1.getEntity();



    }
}