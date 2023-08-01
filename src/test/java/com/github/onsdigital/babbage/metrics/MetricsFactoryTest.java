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
    public void testInitMetricsEnabled() throws Exception {
        //Given
        TestsUtil.setPrivateStaticField(metricsFactory, "metrics", null);
        TestsUtil.setPrivateStaticField(metricsFactory, "metricsEnabled", true);
        MetricsFactory.init();

        //When
        Metrics metrics = MetricsFactory.getMetrics();

        //Then
        assertTrue(MetricsFactory.isMetricsEnabled());
        assertNotNull(metrics);
        assertEquals(metrics.getClass().getName(),"com.github.onsdigital.babbage.metrics.CacheMetrics");
    }

    @Test
    public void testInitMetricsNotEnabled() throws Exception {
        //Given
        TestsUtil.setPrivateStaticField(metricsFactory, "metrics", null);
        TestsUtil.setPrivateStaticField(metricsFactory, "metricsEnabled", false);
        MetricsFactory.init();

        //When
        Metrics metrics = MetricsFactory.getMetrics();

        //Then
        assertFalse(MetricsFactory.isMetricsEnabled());
        assertNotNull(metrics);
        assertEquals(metrics.getClass().getName(),"com.github.onsdigital.babbage.metrics.NopMetricsImpl");
    }

}
