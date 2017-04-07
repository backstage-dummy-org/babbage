package com.github.onsdigital.babbage.request.handler;

import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.response.BabbageContentBasedStringResponse;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.SearchResults;
import com.github.onsdigital.babbage.search.model.filter.PrefixFilter;
import com.github.onsdigital.babbage.search.model.sort.SortField;
import com.github.onsdigital.babbage.util.URIUtil;
import org.elasticsearch.search.sort.SortOrder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.onsdigital.babbage.util.RequestUtil.getQueryParameters;

// only handle requests that specify the CDID and not source dataset.
// ie.
// handle
//    /timeseries/cdid
// and
//    /timeseries/cdid/data
// do not handle
//    /timeseries/cdid/datasetId*
public class TimeseriesLandingRequestHandler extends BaseRequestHandler {

    // regex breakdown:
    // .*/timeseries/  - match anything with /timeseries/
    // [^/]*           - match any number of characters that are not '/' (if there is a forward slash without /data, there is an extra segment of url and implies its not a landing page)
    // (/data)?        - match zero or one instances of '/data'
    // /?              - match zero or one forward slash's on the end of the url
    private static final String landingPageRegex = ".*/timeseries/[^/]*(/data)?/?";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {



        // use the original request uri, as the uri passed in has the last segment removed.
        uri = URIUtil.cleanUri(request.getRequestURI());

        boolean isDataRequest = false;
        if (URIUtil.isDataRequest(uri)) {
            isDataRequest = true;
            uri = URIUtil.removeLastSegment(uri);
        }

        String latestTimeseriesUri = getLatestTimeseriesUri(uri);

        if (isDataRequest) {
            ContentResponse contentResponse = ContentClient.getInstance().getContent(latestTimeseriesUri, getQueryParameters(request));
            return new BabbageContentBasedStringResponse(contentResponse, contentResponse.getAsString());
        } else {
            return PageRequestHandler.getPage(latestTimeseriesUri, request);
        }
    }

    public static String getLatestTimeseriesUri(String uri) {
        // search the timeseries that live under this CDID
        final SearchParam searchParam = SearchParamFactory.getInstance();
        searchParam.addDocType(ContentType.timeseries)
                .addQueryType(QueryType.SEARCH)
                .addFilter(new PrefixFilter(uri))
                .setSize(1)
                .setPage(1)
                .setSortBy(SortBy.first_letter);
        // Filter on dates!!!

        Map<String, SearchResult> results = null;
        try {
            results = SearchUtils.search(searchParam);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }



        // the first timeseries result in the list is the most recent so use that.
        return results.get(QueryType.SEARCH).getResults().get(0).get("uri").toString();
    }

    @Override
    public String getRequestType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean canHandleRequest(String uri, String requestType) {
        return isTimeseriesLandingUri(uri);
    }

    public static boolean isTimeseriesLandingUri(String uri) {
        Pattern r = Pattern.compile(landingPageRegex);
        Matcher match = r.matcher(uri);
        return match.matches();
    }

    public static boolean isTimeseriesLandingDataUri(String uri) {
        Pattern r = Pattern.compile(landingPageRegex);
        Matcher match = r.matcher(uri);
        if (match.find()) {
            String group = match.group(1);
            if (group != null) {
                return true;
            }
        }
        return false;
    }
}
