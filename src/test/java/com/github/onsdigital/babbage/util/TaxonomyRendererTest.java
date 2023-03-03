package com.github.onsdigital.babbage.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


public class TaxonomyRendererTest extends TestCase {

    public static Map<String, Object> ItemMapCensus () {
        Map<String, Object> testItemMap = new HashMap<String, Object>();
        testItemMap.put("label", "Census");
        testItemMap.put("name", "census");
        testItemMap.put("title", "Census");
        testItemMap.put("uri", "/census");
        testItemMap.put("links", "{self={href=/topics/census,id=census}}");
        return testItemMap;
    }

    public static Map<String, Object> ItemMapBusinessWithoutSubtopics () {
        Map<String, Object> testItemMap = new HashMap<String, Object>();
        testItemMap.put("description", "Activities of businesses and industry in the UK, including data on the production and trade of goods and services, sales by retailers, characteristics of businesses, the construction and manufacturing sectors, and international trade.");
        testItemMap.put("label", "Business, industry and trade");
        testItemMap.put("links", "{self={href=/topics/businessindustryandtrade, id=businessindustryandtrade}}");
        testItemMap.put("name", "business-industry-and-trade");
        testItemMap.put("title", "Business, industry and trade");
        testItemMap.put("uri", "/businessindustryandtrade");

        return testItemMap;
    }
    public static Map<String, Object> ItemMapBusinessSubtopics () {
        Map<String, Object> testItemMap = new HashMap<String, Object>();
        testItemMap.put("description", "Activities of businesses and industry in the UK, including data on the production and trade of goods and services, sales by retailers, characteristics of businesses, the construction and manufacturing sectors, and international trade.");
        testItemMap.put("label", "Business, industry and trade");
        testItemMap.put("links", "{self={href=/topics/businessindustryandtrade, id=businessindustryandtrade}}");
        testItemMap.put("name", "business-industry-and-trade");
        testItemMap.put("title", "Business, industry and trade");
        testItemMap.put("uri", "/businessindustryandtrade");
        testItemMap.put("subtopics", ItemListBusinessSubtopics());
        return testItemMap;
    }
    public static List<Map<String, Object>> ItemListBusinessSubtopics () {

        List<Map<String, Object>> subtopicsList = new ArrayList<>();
        Map<String, Object> testSubtopicsMap = new HashMap<String,Object>();

        testSubtopicsMap.put("description", "UK businesses registered for VAT and PAYE with regional breakdowns, including data on size (employment and turnover) and activity (type of industry), research and development, and business services.");
        testSubtopicsMap.put("label", "Business");
        testSubtopicsMap.put("links", "{self={href=/topics/business, id=business}}");
        testSubtopicsMap.put("name", "business");
        testSubtopicsMap.put("title", "Business");
        testSubtopicsMap.put("uri", "/businessindustryandtrade/business");
        subtopicsList.add(testSubtopicsMap);

        return subtopicsList;
    }

    public static Map<String, Object> ItemMapBusinessSubtopicsEmpty () {
        Map<String, Object> testSubtopicsMap = new HashMap<String,Object>();
        return testSubtopicsMap;
    }

    @Test
    public void testNavigationToTaxonomy_NullInput() throws IOException, URISyntaxException {
        List<Map<String, Object>> contextTest = new ArrayList<>();
        try {
            contextTest = TaxonomyRenderer.navigationToTaxonomy(null);
        } catch (Exception ex) {
            assertThat("Incorrect exception message", ex.getMessage(),
                    equalTo("item is null"));
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNavigationToTaxonomy_EmptyInput() throws IOException, URISyntaxException {

        List<Map<String, Object>> contextTest = new ArrayList<>();

        List<Map<String, Object>> testMap = new ArrayList<Map<String, Object>>();

        try {
            Object itemsTestNull = new Object();

            contextTest = TaxonomyRenderer.navigationToTaxonomy(testMap);
        } catch (Exception ex) {
            assertThat("Incorrect exception message", ex.getMessage(),
                    equalTo("item is null"));
            throw ex;
        }
        assertTrue(contextTest.isEmpty());
    }


    @Test(expected = NullPointerException.class)
    public void testNavigationToTaxonomy_InputCensus() throws IOException, URISyntaxException {
        List<Map<String, Object>> contextTest = new ArrayList<>();
        List<Map<String, Object>> testMap = new ArrayList<Map<String, Object>>();
        testMap.add(ItemMapCensus());

        try {
            Object itemsTestNull = new Object();
            contextTest = TaxonomyRenderer.navigationToTaxonomy(testMap);
        } catch (Exception ex) {
            assertThat("Incorrect exception message", ex.getMessage(),
                    equalTo("item is null"));
            throw ex;
        }
        assertTrue(contextTest.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testNavigationToTaxonomy_CensusAndEmpty() throws IOException, URISyntaxException {
        List<Map<String, Object>> contextTest = new ArrayList<>();
        List<Map<String, Object>> testItemList = new ArrayList<Map<String, Object>>();
        testItemList.add(ItemMapCensus());
        Map<String, Object> testSubtopicsMap = new HashMap<String,Object>();
        testItemList.add(testSubtopicsMap);
        assertFalse(testItemList.isEmpty());
        assertTrue(testItemList.size() == 2);

        try {
            Object itemsTestNull = new Object();
            contextTest = TaxonomyRenderer.navigationToTaxonomy(testItemList);
        } catch (Exception ex) {
            assertThat("Incorrect exception message", ex.getMessage(),
                    equalTo("item is null"));
            throw ex;
        }
        assertTrue(contextTest.isEmpty());
    }

    public void testNavigationToTaxonomy_Input() throws IOException, URISyntaxException {
        List<Map<String, Object>> contextTest = new ArrayList<>();
        List<Map<String, Object>> testItemList = new ArrayList<Map<String, Object>>();

        testItemList.add(ItemMapBusinessWithoutSubtopics());
        assertFalse(testItemList.isEmpty());
        assertTrue(testItemList.size() == 1);
        try {
            Object itemsTestNull = new Object();
            contextTest = TaxonomyRenderer.navigationToTaxonomy(testItemList);
        } catch (Exception ex) {
            assertThat("Incorrect exception message", ex.getMessage(),
                    equalTo("item is null"));
            throw ex;
        }
        assertFalse(contextTest.isEmpty());
        assertThat(contextTest.get(0).get("uri"), equalTo("/businessindustryandtrade") );
        assertThat(contextTest.get(0).get("type"), equalTo("taxonomy_landing_page") );
        Map<String, Object> tmpMap = new HashMap<String,Object>();
        tmpMap.put("title", "Business, industry and trade");
        assertThat(contextTest.get(0).get("description"), equalTo(tmpMap));
        List<?> tmpList = new ArrayList();
        assertThat(contextTest.get(0).get("children"), equalTo(tmpList));
    }

    public void testNavigationToTaxonomy_Input2() throws IOException, URISyntaxException {
        List<Map<String, Object>> contextTest = new ArrayList<>();
        List<Map<String, Object>> testItemList = new ArrayList<Map<String, Object>>();

        testItemList.add(ItemMapBusinessSubtopics());
        assertFalse(testItemList.isEmpty());
        assertTrue(testItemList.size() == 1);

        try {
            Object itemsTestNull = new Object();
            contextTest = TaxonomyRenderer.navigationToTaxonomy(testItemList);
        } catch (Exception ex) {
            assertThat("Incorrect exception message", ex.getMessage(),
                    equalTo("item is null"));
            throw ex;
        }


        assertFalse(contextTest.isEmpty());
        assertThat(contextTest.get(0).get("uri"), equalTo("/businessindustryandtrade") );
        assertThat(contextTest.get(0).get("type"), equalTo("taxonomy_landing_page") );
        Map<String, Object> tmpMap = new HashMap<String,Object>();
        tmpMap.put("title", "Business, industry and trade");
        assertThat(contextTest.get(0).get("description"), equalTo(tmpMap));
        List<Map<String, Object>> itemsArray = new ArrayList();
        itemsArray = (ArrayList) contextTest.get(0).get("children");
        assertTrue(itemsArray.size() == 1);



        Map<String, Object> child = new HashMap<>();
        child = itemsArray.get(0);
        assertTrue(child.size() == 3);



    }
}