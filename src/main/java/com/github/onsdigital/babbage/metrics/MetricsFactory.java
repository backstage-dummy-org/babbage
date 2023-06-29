package com.github.onsdigital.babbage.metrics;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

public class MetricsFactory {

    private static Metrics metrics;

    private static boolean metricsEnabled = appConfig().babbage().getMetricsEnabled();

    public static void init() throws Exception {
        if (metrics != null) {
            throw new Exception("Init already called");
        }

        if (metricsEnabled) {
            metrics = new CacheMetrics();
        } else {
            metrics = new NopMetricsImpl();
        }
    }

    public static boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    //Use getMetrics method to get the static metrics object
    //If metrics are enabled then it be of type CacheMetrics
    //Or if metrics are not enabled then it will be of type NopMetricsImpl
    public static Metrics getMetrics(){
         return metrics;
    }

}
