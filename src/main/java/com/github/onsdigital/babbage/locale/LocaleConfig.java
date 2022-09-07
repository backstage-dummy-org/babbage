package com.github.onsdigital.babbage.locale;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Provides cached access to locale specific labels as maps.
 */
public class LocaleConfig {

    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(new Locale("en"), new Locale("cy"));

    /**
     * Fetch a map of labels for a given locale.
     *
     * @param locale
     * @return
     */
    public static Map<String, String> getLabels(Locale locale) {
        Properties properties = loadProperties(locale);
        Map<String, String> labels = toMap(properties);
        return labels;
    }
 
    private static Properties loadProperties(Locale locale) {
        if (!getDefaultLocale().equals(locale)) {
            try {
                return LoadProperties("LabelsBundle_" + locale.getLanguage() + ".properties");
            } catch (IOException e) {
                // Error finding properties. No problem, let's try the default language...
            }
        }

        // Default language (English)
        try {
            return LoadProperties("LabelsBundle.properties");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load search properties file", e);
        }
    }

    private static Properties LoadProperties(String filename) throws IOException {
        Properties properties = new Properties();
        InputStream utf8in = LocaleConfig.class.getClassLoader().getResourceAsStream(filename);
        Reader reader = new InputStreamReader(utf8in, "UTF-8");
        properties.load(reader);
        return properties;
    }


    /**
     * return the locale instance for the given language code.
     * @return
     */
    public static Collection<Locale> getSupportedLanguages() {
        return SUPPORTED_LOCALES;
    }

    public static Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }

    private static Map<String, String> toMap(Properties properties) {
        Map<String, String> map = new HashMap<>();

        Enumeration<?> keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            map.put(key, properties.getProperty(key));
        }

        return map;
    }
}
