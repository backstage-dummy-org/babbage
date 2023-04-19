package com.github.onsdigital.babbage.metrics;

public class MetricsFactory {

    private static CacheMetrics cacheMetrics;

    public static void init() throws Exception {
        if (cacheMetrics != null) {
            throw new Exception("Init already called");
        }

        cacheMetrics = new CacheMetrics();
    }

    //Use getMetrics method to get object of type Metrics
    //If metrics are enabled then it will return the static object of type CacheMetrics
    //Or if metrics are not enabled then it will return an object of type NopMetricsImpl
    public static Metrics getMetrics(boolean metricsEnabled){
        if (metricsEnabled) {
            return MetricsFactory.cacheMetrics;
        } else {
            return new NopMetricsImpl();
        }
    }

}
