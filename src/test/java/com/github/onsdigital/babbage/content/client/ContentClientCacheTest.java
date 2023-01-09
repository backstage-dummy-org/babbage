package com.github.onsdigital.babbage.content.client;


import com.github.onsdigital.babbage.util.http.ClientConfiguration;
import junit.framework.TestCase;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ContentClientCacheTest extends TestCase {


    @Mock
    private ContentClientCache contentClientCache_mock;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setMaxTotalConnection(5000);
        configuration.setDisableRedirectHandling(true);
    }

    public void testGetInstance() {
        ContentClientCache actual = contentClientCache_mock.getInstance();
        assertThat(actual, is(notNullValue()));
    }

    public void testGetContent() throws ContentReadException {
        String uri = "1235";
        HashMap<String, String[]> queryParameters = new HashMap<>();
        String[] mockList = {"E1", "E2", "E3", "E4"};
        queryParameters.put("England",mockList);
        queryParameters.put("Wales",mockList);
        ContentResponse configuration = contentClientCache_mock.getContent(uri,queryParameters);
        assertThat(configuration, is(nullValue()));
    }

}