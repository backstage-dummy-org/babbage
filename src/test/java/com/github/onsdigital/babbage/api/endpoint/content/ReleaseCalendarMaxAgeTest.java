package com.github.onsdigital.babbage.api.endpoint.content;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class ReleaseCalendarMaxAgeTest {
    private final static String KEY_HASH = "the-key";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    @Spy
    private ReleaseCalendarMaxAge endpoint;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(endpoint.verifyKey(KEY_HASH)).thenReturn(true);
    }

    @Test
    public void testGetNoKey() throws Exception {
        Object result = endpoint.get(request, response);
        assertEquals("Wrong key, make sure you pass in the right key", result);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetWrongKey() throws Exception {
        when(request.getParameter("key")).thenReturn("wrong");

        Object result = endpoint.get(request, response);
        assertEquals("Wrong key, make sure you pass in the right key", result);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEndpoint() throws Exception {
        when(request.getParameter("key")).thenReturn(KEY_HASH);
        
        int expectedMaxAge = 100;
        
        Instant nextReleaseDate = Instant.now().plusSeconds(expectedMaxAge);
        doReturn(nextReleaseDate).when(endpoint).getNextReleaseDate();

        int maxAge = (int) endpoint.get(request, response);
        // Check max age is as expected with 1 second tolerance
        assertTrue(expectedMaxAge-maxAge < 2);
    }

    @Test
    public void testGetEndpointWithoutNextRelease() throws Exception {
        when(request.getParameter("key")).thenReturn(KEY_HASH);

        doReturn(null).when(endpoint).getNextReleaseDate();

        int expectedMaxAge = appConfig().babbage().getDefaultContentCacheTime();
        assertEquals(expectedMaxAge, endpoint.get(request, response));
    }

    @Test
    public void testGetEndpointNextReleaseInThePast() throws Exception {
        // This scenario should never happen but added for completeness
        when(request.getParameter("key")).thenReturn(KEY_HASH);

        Instant nextReleaseDate = Instant.now().minusSeconds(10);
        doReturn(nextReleaseDate).when(endpoint).getNextReleaseDate();

        int expectedMaxAge = appConfig().babbage().getDefaultContentCacheTime();
        assertEquals(expectedMaxAge, endpoint.get(request, response));
    }
}
