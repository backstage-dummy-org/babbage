package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.util.SearchParam;
import com.github.onsdigital.babbage.api.util.SearchParamFactory;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.request.handler.base.BaseRequestHandler;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.input.TypeFilter;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.QueryType;
import com.github.onsdigital.babbage.search.model.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.github.onsdigital.babbage.api.util.SearchUtils.*;
import static com.github.onsdigital.babbage.search.builders.ONSFilterBuilders.filterTopic;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.*;

/**
 * Created by bren on 21/09/15.
 */
public class AllMethodologiesRequestHandler extends BaseRequestHandler implements ListRequestHandler {

    private static Set<TypeFilter> methodologyFilters = TypeFilter.getMethodologyFilters();

    private final static String REQUEST_TYPE = "allmethodologies";

    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        return buildPageResponse(REQUEST_TYPE, search(request));
    }

    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException, URISyntaxException {
        return buildDataResponse(REQUEST_TYPE, search(request));
    }

    private Map<String, SearchResult> search(HttpServletRequest request) throws IOException, URISyntaxException {
        final SearchParam params = SearchParamFactory.getInstance(request, SortBy.release_date, Collections.singleton(QueryType.SEARCH));
        params.addTypeFilters(methodologyFilters);
        final Map<String, SearchResult> results = SearchUtils.search(params);
        return results;
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }
}
