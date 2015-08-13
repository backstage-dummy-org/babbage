package com.github.onsdigital.page;

import com.github.onsdigital.content.page.base.Page;
import com.github.onsdigital.content.page.statistics.document.figure.table.Table;
import com.github.onsdigital.content.service.ContentNotFoundException;
import com.github.onsdigital.content.service.ContentRenderingService;
import com.github.onsdigital.content.util.markdown.ChartTagReplacer;
import com.github.onsdigital.content.util.markdown.TableTagReplacer;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.data.zebedee.ZebedeeRequest;
import com.github.onsdigital.template.TemplateService;
import com.github.onsdigital.util.NavigationUtil;
import com.github.onsdigital.util.LocaleUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ContentRenderer implements ContentRenderingService {

    private final ZebedeeRequest zebedeeRequest;
    private final boolean jsEnhanced;
    private final Locale locale;

    public ContentRenderer(ZebedeeRequest zebedeeRequest) {
        this.zebedeeRequest = zebedeeRequest;
        this.jsEnhanced = false;
        this.locale = Locale.ENGLISH;
    }

    public ContentRenderer(ZebedeeRequest zebedeeRequest, boolean jsEnhanced) {
        this.zebedeeRequest = zebedeeRequest;
        this.jsEnhanced = jsEnhanced;
        this.locale = Locale.ENGLISH;
    }

    public ContentRenderer(ZebedeeRequest zebedeeRequest, boolean jsEnhanced, Locale locale) {
        this.zebedeeRequest = zebedeeRequest;
        this.jsEnhanced = jsEnhanced;
        this.locale = locale;
    }

    @Override
    public String renderChart(String uri, boolean partial) throws IOException, ContentNotFoundException {
        Page page = DataService.getInstance().readAsPage(uri, false, zebedeeRequest);

        String template = "chart";

        if (partial) {
            template = "partials/" + template;
        }

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("jsEnhanced", jsEnhanced);

        return TemplateService.getInstance().render(page, template, additionalData);
    }

    @Override
    public String renderTable(String uri, boolean partial) throws IOException, ContentNotFoundException {

        DataService dataService = DataService.getInstance();

        Page page = dataService.readAsPage(uri + ".json", false, zebedeeRequest);
        String tableHtml = IOUtils.toString(dataService.readData(uri + ".html", false, zebedeeRequest));

        if (page instanceof Table) {
            ((Table) page).setHtml(tableHtml);
        }

        String template = "table";

        if (partial) {
            template = "partials/" + template;
        }

        return TemplateService.getInstance().render(page, template);
    }

    @Override
    public String renderPage(String uri) throws IOException, ContentNotFoundException {
        Page page = DataService.getInstance().readAsPage(uri, true, zebedeeRequest);

        page.setNavigation(NavigationUtil.getNavigation());

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("jsEnhanced", jsEnhanced);
        additionalData.put("labels", LocaleUtil.getLabels(this.locale));

        String pageContent = TemplateService.getInstance().renderPage(page, additionalData);

        pageContent = (new TableTagReplacer()).replaceCustomTags(pageContent, this);
        pageContent = (new ChartTagReplacer()).replaceCustomTags(pageContent, this);

        return pageContent;
    }
}
