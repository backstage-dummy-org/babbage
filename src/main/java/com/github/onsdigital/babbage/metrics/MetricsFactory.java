package com.github.onsdigital.babbage.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

public class MetricsFactory {

    private static CacheMetrics metrics;

    public static void init() throws Exception {
        if (metrics != null) {
            throw new Exception("Init already called");
        }

        metrics = new CacheMetrics();

        metrics.httpServer = new HTTPServer.Builder()
                .withPort(appConfig().babbage().getMetricsPort())
                .build();

        metrics.publishDatePresent = Counter.build()
                .name("publish_date_present").help("Total requests for uris that have a past or future publishing date").register();
        metrics.publishDateNotPresent = Counter.build()
                .name("publish_date_not_present").help("Total requests for uris that have no publishing date found").register();
        metrics.publishDateInFuture = Counter.build()
                .name("publish_date_in_future").help("Total requests for uris that have a future publishing date").register();
        metrics.publishDateTooFarInPast = Counter.build()
                .name("publish_date_too_far_in_past").help("Total requests for uris that have a past publishing date too long ago (outside a given time span)").register();
        metrics.cacheExpiryTime = Gauge.build()
                .name("cache_expiry_time").help("The time until the cache expires and will be refreshed by another call to the server.").labelNames("is_greater_than_default").register();
    }

    //Use getMetrics method to get object of type Metrics
    //If metrics are enabled then it will return the static object of type CacheMetrics
    //Or if metrics are not enabled then it will return an object of type NopMetricImpl
    public static Metrics getMetrics(boolean metricsEnabled){
        if (metricsEnabled) {
            return MetricsFactory.metrics;
        } else {
            return null;
        }
    }

}
