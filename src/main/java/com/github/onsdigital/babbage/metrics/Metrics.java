package com.github.onsdigital.babbage.metrics;

public interface Metrics {

    void incPublishDateInRange();
    void incPublishDateNotPresent();
    void incPublishDateTooFarInPast();
    void incPublishDateTooFarInFuture();
}
