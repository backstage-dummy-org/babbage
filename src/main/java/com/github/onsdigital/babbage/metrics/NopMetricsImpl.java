package com.github.onsdigital.babbage.metrics;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

public class NopMetricsImpl implements Metrics {

    public void incPublishDateInRange() {
        info().log("NopMetricsImpl incPublishDateInRange");
    }

    public void incPublishDateNotPresent() {
        info().log("NopMetricsImpl incPublishDateNotPresent");
    }

    public void incPublishDateTooFarInPast() {
        info().log("NopMetricsImpl incPublishDateTooFarInPast");
    }

    public void incPublishDateTooFarInFuture() {
        info().log("NopMetricsImpl incPublishDateTooFarInFuture();");
    }
}
