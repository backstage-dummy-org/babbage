package com.github.onsdigital.babbage.metrics;

import com.github.onsdigital.babbage.util.TestsUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MetricsFactoryTest {

    // Test target.
    private MetricsFactory metricsFactory;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        metricsFactory = new MetricsFactory();
//        TestsUtil.setPrivateStaticField(metricsFactory, "metrics", new NopMetricsImpl());
    }

    @Test
    public void testInitAlreadyCalled() throws Exception {
        //Given
        metricsFactory = new MetricsFactory();
        TestsUtil.setPrivateStaticField(metricsFactory, "metrics", new NopMetricsImpl());

        // Then
        expectedException.expect(Exception.class);
        expectedException.expectMessage("Init already called");
        metricsFactory.init();

    }

}