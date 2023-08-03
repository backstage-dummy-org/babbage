package com.github.onsdigital.babbage.api.endpoint;

import com.github.davidcarboni.restolino.framework.Api;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.logging.v2.event.SimpleEvent.warn;

@Api
public class Metrics {
    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response)
            throws IOException {
        if (appConfig().babbage().getMetricsEnabled()) {
            Writer writer = new StringWriter();
            if (appConfig().babbage().getMetricsFormat().equalsIgnoreCase("Open")) {
                TextFormat.writeOpenMetrics100(writer,
                        CollectorRegistry.defaultRegistry.metricFamilySamples());
                response.setContentType(
                        "application/openmetrics-text; version=1.0.0; charset=utf-8");
            } else if (appConfig().babbage().getMetricsFormat().equalsIgnoreCase("Text")) {
                TextFormat.write004(writer,
                        CollectorRegistry.defaultRegistry.metricFamilySamples());
                response.setContentType("text/plain; version=0.0.4; charset=utf-8");
            } else {
                warn().log(
                        "Metrics log format is incorrectly set falling back to default of: Text");
                TextFormat.write004(writer,
                        CollectorRegistry.defaultRegistry.metricFamilySamples());
                response.setContentType("text/plain; version=0.0.4; charset=utf-8");
            }
            response.getWriter().write(writer.toString());
            response.getWriter().flush();
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }
}
