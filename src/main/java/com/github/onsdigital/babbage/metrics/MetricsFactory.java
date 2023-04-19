package com.github.onsdigital.babbage.metrics;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

public class MetricsFactory {

//    private static CacheMetrics cacheMetrics;
    private static Metrics metrics;

    public static void init() throws Exception {
//        if (cacheMetrics != null) {
//            throw new Exception("Init already called");
//        }

        if (metrics != null) {
            throw new Exception("Init already called");
        }

//        cacheMetrics = new CacheMetrics();
        if (appConfig().babbage().getMetricsEnabled()) {
            metrics = new CacheMetrics();
        } else {
            metrics = new NopMetricsImpl();
        }
    }

    //Use getMetrics method to get object of type Metrics
    //If metrics are enabled then it will return the static object of type CacheMetrics
    //Or if metrics are not enabled then it will return an object of type NopMetricsImpl
    public static Metrics getMetrics(boolean metricsEnabled){
//        if (metricsEnabled) {
//            return MetricsFactory.cacheMetrics;
//        } else {
//            return new NopMetricsImpl();
//        }
         return MetricsFactory.metrics;
    }

}
