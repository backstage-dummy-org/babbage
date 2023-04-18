package com.github.onsdigital.babbage.metrics;

public interface Metrics {

//    Metrics get();
    void incPublishDatePresent();
    void incPublishDateNotPresent();
    void incPublishDateInFuture();
    void incPublishDateTooFarInPast();
    void setCacheExpiryTime(Double expiryTime);
}
