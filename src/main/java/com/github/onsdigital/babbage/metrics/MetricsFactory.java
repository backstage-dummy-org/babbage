package com.github.onsdigital.babbage.metrics;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;


public class MetricsFactory {

    private static Metrics metrics;

    private static boolean metricsEnabled = appConfig().babbage().getMetricsEnabled();

    public static void init() {
        if (metrics == null) {
            if (metricsEnabled) {
                info().log("initialising CacheMetrics");
                metrics = new CacheMetrics();
            } else {
                metrics = new NopMetricsImpl();
            }
        } else {
            info().log("CacheMetrics already initialised");
        }
    }

    public static boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    // Use getMetrics method to get the static metrics object
    // If metrics are enabled then it be of type CacheMetrics
    // Or if metrics are not enabled then it will be of type NopMetricsImpl
    public static Metrics getMetrics() {
        return metrics;
    }

}
