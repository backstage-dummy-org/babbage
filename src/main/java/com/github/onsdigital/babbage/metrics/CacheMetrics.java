package com.github.onsdigital.babbage.metrics;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.Counter;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

public class CacheMetrics implements Metrics {

    HTTPServer httpServer;
    Counter publishDatePresent;
    Counter publishDateNotPresent;
    Counter publishDateInFuture;
    Counter publishDateTooFarInPast;
    Gauge cacheExpiryTime;

    public void incPublishDatePresent() {
        publishDatePresent.inc();
    }

    public void incPublishDateNotPresent() {
        publishDateNotPresent.inc();
    }

    public void incPublishDateInFuture() {
        publishDateInFuture.inc();
    }

    public void incPublishDateTooFarInPast() {
        publishDateTooFarInPast.inc();
    }

    public void setCacheExpiryTime(Double expiryTime) {
        boolean isGreaterThanDefault = false;
        if (expiryTime.intValue() > appConfig().babbage().getDefaultContentCacheTime()) {
            isGreaterThanDefault = true;
        }
        cacheExpiryTime.labels(String.valueOf(isGreaterThanDefault)).set(expiryTime);
    }
}
