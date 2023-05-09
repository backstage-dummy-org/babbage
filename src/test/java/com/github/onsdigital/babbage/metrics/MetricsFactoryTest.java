package com.github.onsdigital.babbage.metrics;

import com.github.onsdigital.babbage.configuration.Babbage;
import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;


public class MetricsFactoryTest {

    // Test target.
    private MetricsFactory metricsFactory;

//    @Mock
//    private ApplicationConfiguration appConfigurationMock;

    @Mock
    private Babbage babbageMock;

    @Before
    public void setup() {
//        when(appConfigurationMock.babbage().getMetricsEnabled()).thenReturn(true);
        MockitoAnnotations.initMocks(this);
//        when(appConfig().babbage()).thenReturn(babbageMock);
        when(babbageMock.getMetricsEnabled()).thenReturn(true);
        metricsFactory = new MetricsFactory();
    }

    @Test
    public void testInitAlreadyCalled() throws Exception {
        //Given
        TestsUtil.setPrivateStaticField(metricsFactory, "metrics", new NopMetricsImpl());

        // Then
        Exception exception = assertThrows(Exception.class,
                () -> MetricsFactory.init());
        assertTrue(exception.getMessage().contains("Init already called"));
    }

    @Test
    public void testInitNotAlreadyCalled() throws Exception {
        //Given
//        when(appConfigurationMock.babbage()).thenReturn(babbageMock);
//        when(babbageMock.getMetricsEnabled()).thenReturn(true);
//        when(appConfigurationMock.babbage().getMetricsEnabled()).thenReturn(true);
        MetricsFactory.isMetricsEnabled = true;
        MetricsFactory.init();

        //When
        Metrics metrics = MetricsFactory.getMetrics();

        //Then
        assertTrue(MetricsFactory.isMetricsEnabled);
        assertNotNull(metrics);
        assertEquals(metrics.getClass().getName(),"com.github.onsdigital.babbage.metrics.CacheMetrics");
    }

}