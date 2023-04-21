package com.github.onsdigital.babbage.metrics;

import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MetricsFactoryTest {

    // Test target.
    private MetricsFactory metricsFactory;

    @Before
    public void setup() {
        metricsFactory = new MetricsFactory();
    }

    @Test
    public void testInitAlreadyCalled() throws Exception {
        //Given
        TestsUtil.setPrivateStaticField(metricsFactory, "metrics", new NopMetricsImpl());

        // Then
        Exception exception = assertThrows(Exception.class,
                () -> metricsFactory.init());
        assertTrue(exception.getMessage().contains("Init already called"));
    }

    @Test
    public void testInitNotAlreadyCalled() throws Exception {
        //Given
        metricsFactory.init();

        //When
        Metrics metrics = metricsFactory.getMetrics();

        //Then
        assertNotNull(metrics);
        assertEquals(metrics.getClass().getName(),"com.github.onsdigital.babbage.metrics.CacheMetrics");
    }

}