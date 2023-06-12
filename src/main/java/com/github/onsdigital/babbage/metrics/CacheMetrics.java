package com.github.onsdigital.babbage.metrics;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.Counter;

import java.io.IOException;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

public class CacheMetrics implements Metrics {

    private final Counter publishDateInRange;
    private final Counter publishDateNotPresent;
    private final Counter publishDateTooFarInPast;
    private final Counter publishDateTooFarInFuture;

    public CacheMetrics() throws IOException {
        new HTTPServer.Builder().withPort(appConfig().babbage().getMetricsPort()).build();

        this.publishDateInRange = Counter.build()
                .name("publish_date_in_range").help("Total requests for uris that have a publishing date within the range required for setting the cache expiry time").register();
        this.publishDateNotPresent = Counter.build()
                .name("publish_date_not_present").help("Total requests for uris that have no publishing date found").register();
        this.publishDateTooFarInPast = Counter.build()
                .name("publish_date_too_far_in_past").help("Total requests for uris that have a past publishing date too long ago (outside a given time span)").register();
        this.publishDateTooFarInFuture = Counter.build().name("publish_date_too_far_in_future").help("Total requests for uris that have a future publishing date later than that calculated by the default expiry time").register();
    }

    public void incPublishDateInRange() {
        publishDateInRange.inc();
    }

    public void incPublishDateNotPresent() {
        publishDateNotPresent.inc();
    }

    public void incPublishDateTooFarInPast() {
        publishDateTooFarInPast.inc();
    }

    public void incPublishDateTooFarInFuture() { publishDateTooFarInFuture.inc(); }
}
