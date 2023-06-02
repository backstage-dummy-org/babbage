package com.github.onsdigital.babbage.metrics;

public interface Metrics {

    void incPublishDatePresent();
    void incPublishDateNotPresent();
    void incPublishDateInFuture();
    void incPublishDateTooFarInPast();
    void incPublishDateTooFarInFuture();

}
