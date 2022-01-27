package com.github.onsdigital.babbage.template;

import com.github.onsdigital.babbage.template.handlebars.HandlebarsRenderer;
import com.github.onsdigital.babbage.util.ThreadContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static com.github.onsdigital.babbage.util.json.JsonUtil.toMap;

/**
 * Created by bren on 28/05/15. Resolves data type and renders html page.
 * <p/>
 * Template service includes current {@link com.github.onsdigital.babbage.util.ThreadContext} in the context with key value names
 */
public class TemplateService {

    private static TemplateService instance = new TemplateService();

    private static HandlebarsRenderer renderer = new HandlebarsRenderer(appConfig().handlebars().getTemplatesDirectory(),
            appConfig().handlebars().getTemplatesSuffix(), appConfig().handlebars().isReloadTemplateChanges());

    private TemplateService() {
    }

    public static TemplateService getInstance() {
        return instance;
    }

    
    /**
     * Renders data using main template, current ThreadContext data is added to context as additional data
     *
     * @param data           Main data to render template with
     * @return
     * @throws IOException
     */
    public String renderContent(Object data) throws IOException {
        return renderContent(data, null);
    }
    
    /**
     * Renders data using main template, current ThreadContext data is added to context as additional data
     *
     * @param data           Main data to render template with
     * @param additionalData additional data map, map keys will be set as the object name when combined with main data
     * @return
     * @throws IOException
     */
    public String renderContent(Object data, Map<String, Object> additionalData) throws IOException {
        String templateName = appConfig().handlebars().getMainContentTemplateName();
        return renderTemplate(templateName, data, additionalData);
    }

    /**
     * Renders chart configuration using main chart configuration template, current ThreadContext data is added to context as additional data
     *
     * @param data           Main data to render template with
     * @param additionalData additional data map, map keys will be set as the object name when combined with main data
     * @return
     * @throws IOException
     */
    public String renderChartConfiguration(Object data, Map<String, Object> additionalData) throws IOException {
        String templateName = appConfig().handlebars().getMainChartConfigTemplateName();
        return renderTemplate(templateName, data, additionalData);
    }

    /**
     * Renders template with no data other than current thread context
     *
     * @param templateName
     * @return
     * @throws IOException
     */
    public String renderTemplate(String templateName) throws IOException {
        return renderTemplate(templateName, Collections.emptyMap());
    }

    /**
     * Renders template with given name using given data and current thread context
     *
     * @param templateName
     * @return
     * @throws IOException
     */
    public String renderTemplate(String templateName, Object data) throws IOException {
        return renderTemplate(templateName, data, null);
    }

    /**
     * Renders template with given name using given data, current ThreadContext data is added to context as additional data
     *
     * @param templateName
     * @param data
     * @param additionalData additional data
     * @return
     * @throws IOException
     */
    public String renderTemplate(String templateName, Object data, Map<String, Object> additionalData) throws IOException {
        return renderer.render(templateName, sanitize(data), addThreadContext(additionalData));
    }

    /*Converts object into map if json string or json input stream*/
    private static Object sanitize(Object data) throws IOException {
        if (data instanceof String) {
            return toMap((String) data);
        } else if (data instanceof InputStream) {
            return toMap((InputStream) data);
        } else {
            return data;
        }
    }

    /**
     * Returns a new map containing all elements in the map passed as parameter plus the current thread context
     *
     * @param data
     * @return
     */
    private Map<String, Object> addThreadContext(Map<String, Object> data) {
        Map<String, Object> r = new HashMap<String, Object>();
        if (data != null) {
            r.putAll(data);
        }
        r.putAll(ThreadContext.getAllData());
        return r;
    }

}
