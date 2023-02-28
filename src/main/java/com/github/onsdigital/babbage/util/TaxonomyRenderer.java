package com.github.onsdigital.babbage.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


public class TaxonomyRenderer {
    static List<String> topicsToIgnore = Arrays.asList("Census", "Survey");

    public static List<Map<String, Object>> navigationToTaxonomy(InputStream jsonData) throws IOException, URISyntaxException {

        List<Map<String, Object>> context = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(IOUtils.toString(jsonData)).getAsJsonObject();
        JsonArray items = (JsonArray) obj.get("items");

        for (JsonElement item : items) {

            JsonObject itemJsonObject = item.getAsJsonObject();
            if (!(topicsToIgnore.contains(itemJsonObject.get("title").getAsString()))) {
                Map<String, Object> itemMap = new HashMap<String, Object>();
                Map<String, Object> itemDescription = new HashMap<String, Object>();

                itemMap.put("uri", itemJsonObject.get("uri").toString().replaceAll("\"", ""));
                itemDescription.put("title", itemJsonObject.get("title").toString().replaceAll("\"", ""));
                itemMap.put("description", itemDescription);
                itemMap.put("type", "taxonomy_landing_page");

                List<Map<String, Object>> childrenArray = new ArrayList<>();
                JsonArray subtopics = itemJsonObject.getAsJsonArray("subtopics");
                if (subtopics != null) {
                    for (JsonElement subtopic : subtopics) {
                        JsonObject subtopicJsonObject = subtopic.getAsJsonObject();
                        Map<String, Object> subtopicMap = new HashMap<String, Object>();
                        Map<String, Object> subtopicDescription = new HashMap<String, Object>();

                        subtopicMap.put("uri", subtopicJsonObject.get("uri").toString().replaceAll("\"", ""));
                        subtopicDescription.put("title", subtopicJsonObject.get("title").toString().replaceAll("\"", ""));
                        subtopicMap.put("description", subtopicDescription);
                        subtopicMap.put("type", "taxonomy_landing_page");
                        childrenArray.add(subtopicMap);
                    }

                }
                itemMap.put("children", childrenArray);
                context.add(itemMap);
            }
        }
        return context;
    }
}
