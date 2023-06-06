package com.github.onsdigital.babbage.configuration;

import junit.framework.TestCase;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class BabbageTest extends junit.framework.TestCase {
    @org.mockito.Mock
    private Babbage mockBabbage;

    @org.junit.Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        when(mockBabbage.isPublishing()).thenReturn(true);
    }

    @org.junit.Test
    public void testGetInstance_testdefaults() {
        Babbage testInstance = Babbage.getInstance();
        assertEquals(testInstance.getDefaultCacheTime(), 900);
        assertEquals(testInstance.getDefaultContentCacheTime(), 900);
        assertEquals(testInstance.getExportSeverUrl(), "http://localhost:9999/");
        assertEquals(testInstance.getMathjaxExportServer(), null);
        assertEquals(testInstance.getMaxHighchartsServerConnections(), 50);
        assertEquals(testInstance.getMaxResultsPerPage(), 250);
        assertEquals(testInstance.getMaxVisiblePaginatorLink(), 5);
        assertEquals(testInstance.getPublishCacheTimeout(), 3600);
        assertEquals(testInstance.getRedirectSecret(), "secret");
        assertEquals(testInstance.getResultsPerPage(), 10);
        assertEquals(testInstance.getSearchResponseCacheTime(), 5);
        assertNotNull(testInstance.getReindexServer());
        assertNotNull(testInstance.getMaxAgeServer());
        assertEquals(testInstance.isCacheEnabled(), false);
        assertEquals(testInstance.isDevEnv(), false);
        assertEquals(testInstance.isDevEnvironment(), false);
        assertEquals(testInstance.isPublishing(), false);

        java.util.Map<String, Object> mockConfig = new java.util.HashMap<>();
        mockConfig = Babbage.getInstance().getConfig();
        assertEquals(mockConfig.get("cacheEnabled"), testInstance.isCacheEnabled());
        assertEquals(mockConfig.get("defaultCacheTime"), testInstance.getDefaultContentCacheTime());
        assertEquals(mockConfig.get("exportSeverUrl"), testInstance.getExportSeverUrl());
        assertEquals(mockConfig.get("isDevEnv"), testInstance.isDevEnv());
        assertEquals(mockConfig.get("isPublishing"), testInstance.isPublishing());
        assertEquals(mockConfig.get("mathjaxExportServer"), testInstance.getMathjaxExportServer());
        assertEquals(mockConfig.get("maxHighchartsServerConnections"), testInstance.getMaxHighchartsServerConnections());
        assertEquals(mockConfig.get("maxResultsPerPage"), testInstance.getMaxResultsPerPage());
        assertEquals(mockConfig.get("maxVisiblePaginatorLink"), testInstance.getMaxVisiblePaginatorLink());
        assertEquals(mockConfig.get("publishCacheTimeout"), testInstance.getPublishCacheTimeout());
        assertEquals(mockConfig.get("resultsPerPage"), testInstance.getResultsPerPage());
        assertEquals(mockConfig.get("searchResponseCacheTime"), testInstance.getSearchResponseCacheTime());
        assertNotNull(mockConfig.get("maxAgeServer"));
        assertNotNull(mockConfig.get("reindexServer"));
    }

}