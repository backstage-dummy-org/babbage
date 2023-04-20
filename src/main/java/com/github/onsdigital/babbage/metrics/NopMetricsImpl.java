package com.github.onsdigital.babbage.metrics;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class NopMetricsImpl implements Metrics {

    public void incPublishDatePresent() {
        info().log("NopMetricsImpl incPublishDatePresent");
    }

    public void incPublishDateNotPresent() { }

    public void incPublishDateInFuture() {
        info().log("NopMetricsImpl incPublishDateInFuture");
    }

    public void incPublishDateTooFarInPast() {
        info().log("NopMetricsImpl incPublishDateTooFarInPast");
    }

    public void setCacheExpiryTime(Double expiryTime) { }
}
