package com.github.onsdigital.babbage.metrics;

import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


public class MetricsFactoryTest {

    // Test target.
    private MetricsFactory metricsFactory;

    @Before
    public void setup() throws Exception {
//        MockitoAnnotations.initMocks(this);
        metricsFactory = new MetricsFactory();
//        TestsUtil.setPrivateStaticField(metricsFactory, "metrics", new NopMetricsImpl());
    }

    @Test
    public void testInitAlreadyCalled() throws Exception {
        //Given
//        metricsFactory = new MetricsFactory();
        TestsUtil.setPrivateStaticField(metricsFactory, "metrics", new NopMetricsImpl());

        // Then
        Exception exception = assertThrows(Exception.class,
                () -> metricsFactory.init());
        assertTrue(exception.getMessage().contains("Init already called"));
    }



}