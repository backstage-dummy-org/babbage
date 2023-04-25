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
                () -> MetricsFactory.init());
        assertTrue(exception.getMessage().contains("Init already called"));
    }

    @Test
    public void testInitNotAlreadyCalled() throws Exception {
        //Given
        MetricsFactory.init();

        //When
        Metrics metrics = MetricsFactory.getMetrics();

        //Then
        assertNotNull(metrics);
        assertEquals(metrics.getClass().getName(),"com.github.onsdigital.babbage.metrics.CacheMetrics");
    }

}