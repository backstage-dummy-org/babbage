package com.github.onsdigital.babbage.template.handlebars.helpers.markdown.util;

import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentStream;
import com.github.onsdigital.babbage.template.TemplateService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the format of the custom markdown tags for charts and defines how to replace them.
 */
public class ChartTagReplacer extends TagReplacementStrategy {

    private static final Pattern pattern = Pattern.compile("<ons-chart\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");

    public ChartTagReplacer(String path) {
        super(path);
    }

    /**
     * Gets the pattern that this strategy is applied to.
     *
     * @return
     */
    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String replace(Matcher matcher) throws IOException {

        String tagPath = matcher.group(1);
        String figureUri = resolveFigureUri(this.getPath(), Paths.get(tagPath));

        try (ContentStream stream = ContentClient.getInstance().getContentStream(figureUri)) {
            return TemplateService.getInstance().renderTemplate("partials/highcharts/chart", stream.getDataStream());
        } catch (ContentReadException e) {
            return matcher.group();
        }
    }

    public static void main(String[] args) {

        Path path = Paths.get("/one/two/three");
        System.out.println(path.getFileName());
        path = Paths.get("/one/two/three/");
        System.out.println(path.getFileName());
        path = Paths.get("/one/two/three.json/");
        System.out.println(path.getFileName());
    }
}
