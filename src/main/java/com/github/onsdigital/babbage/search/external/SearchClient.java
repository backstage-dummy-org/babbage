package com.github.onsdigital.babbage.search.external;

import com.github.onsdigital.babbage.search.external.requests.search.requests.*;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.SearchResult;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.*;

public class SearchClient {

    private static HttpClient client = new HttpClient();

    public static void init() throws Exception {
        System.out.println("Initialising external search client");
        client.start();
        Runtime.getRuntime().addShutdownHook(new Shutdown(client));
        System.out.println("Initialised external search client successfully");
    }

    public static Request request(String uri) {
        return client.newRequest(uri);
    }

    public static Request get(String uri) {
        return request(uri).method(HttpMethod.GET);
    }

    public static Request post(String uri) {
        return request(uri).method(HttpMethod.POST);
    }

    public static LinkedHashMap<String, SearchResult> proxyQueries(List<ONSQuery> queryList) throws Exception {
        Map<String, Future<SearchResult>> futures = new HashMap<>();

        for (ONSQuery query : queryList) {
            ProxyONSQuery request = new ProxyONSQuery(query);
            Future<SearchResult> future = SearchClientExecutorService.getInstance().submit(request);
            futures.put(query.name(), future);
        }

        return processFutures(futures);
    }

    public static LinkedHashMap<String, SearchResult> search(HttpServletRequest request, String listType) throws Exception {
        Map<String, Future<SearchResult>> futures = new HashMap<>();

        final String searchTerm = extractSearchTerm(request);
        final int page = extractPage(request);
        final int pageSize = extractSize(request);
        final SortBy sortBy = extractSortBy(request, ContentQuery.DEFAULT_SORT_BY);

        ListType listTypeEnum = ListType.forString(listType);

        final Set<TypeFilter> typeFilters = extractSelectedFilters(request, listTypeEnum.getTypeFilters(), false);

        SearchType[] searchTypes;
        if (!listTypeEnum.equals(ListType.ONS)) {
            searchTypes = new SearchType[]{SearchType.CONTENT, SearchType.COUNTS};
        } else {
            // Submit content
            searchTypes = SearchType.getBaseSearchTypes();
        }

        for (SearchType searchType : searchTypes) {
            SearchQuery searchQuery;
            switch (searchType) {
                case CONTENT:
                    searchQuery = new ContentQuery(searchTerm, listTypeEnum, page, pageSize, sortBy, typeFilters);
                    break;
                case COUNTS:
                    searchQuery = new TypeCountsQuery(searchTerm, listTypeEnum);
                    break;
                case FEATURED:
                    searchQuery = new FeaturedResultQuery(searchTerm, listTypeEnum);
                    break;
                default:
                    throw new Exception(String.format("Unknown searchType: %s", searchType.getSearchType()));
            }
            // Submit concurrent requests
            Future<SearchResult> future = SearchClientExecutorService.getInstance().submit(searchQuery);
            futures.put(searchType.getResultKey(), future);
        }

        // Wait until complete
        return processFutures(futures);
    }

    private static LinkedHashMap<String, SearchResult> processFutures(Map<String, Future<SearchResult>> futures) throws ExecutionException, InterruptedException {
        // Collect results
        LinkedHashMap<String, SearchResult> results = new LinkedHashMap<>();

        for (String key : futures.keySet()) {
            SearchResult result = futures.get(key).get();
            results.put(key, result);
        }

        return results;
    }

    public static void stop() throws Exception {
        client.stop();
    }

    static class Shutdown extends Thread {
        /**
         * Class to ensure clean shutdown of HttpClient
         */

        private final HttpClient client;

        public Shutdown(HttpClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                this.client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
