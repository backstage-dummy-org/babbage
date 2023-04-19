package com.github.onsdigital.babbage.metrics;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

public class MetricsFactory {

    private static Metrics metrics;

    public static void init() throws Exception {
        if (metrics != null) {
            throw new Exception("Init already called");
        }

        if (appConfig().babbage().getMetricsEnabled()) {
            metrics = new CacheMetrics();
        } else {
            metrics = new NopMetricsImpl();
        }
    }

    //Use getMetrics method to get the static metrics object
    //If metrics are enabled then it be of type CacheMetrics
    //Or if metrics are not enabled then it will be of type NopMetricsImpl
    public static Metrics getMetrics(){
         return MetricsFactory.metrics;
    }

}
