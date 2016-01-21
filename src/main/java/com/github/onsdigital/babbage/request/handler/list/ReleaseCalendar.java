package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.request.handler.base.ListRequestHandler;
import com.github.onsdigital.babbage.response.base.BabbageResponse;
import com.github.onsdigital.babbage.search.builders.ONSFilterBuilders;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.helpers.base.SearchQueries;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

import static com.github.onsdigital.babbage.api.util.SearchUtils.buildListQuery;
import static com.github.onsdigital.babbage.search.builders.ONSQueryBuilders.combine;
import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.extractSearchTerm;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Created by bren on 22/09/15.
 */
public class ReleaseCalendar implements ListRequestHandler {
    private final static String REQUEST_TYPE = "releasecalendar";


    @Override
    public BabbageResponse get(String uri, HttpServletRequest request) throws Exception {
        return SearchUtils.listPage(getClass().getSimpleName(), queries(request));
    }


    @Override
    public BabbageResponse getData(String uri, HttpServletRequest request) throws IOException, ContentReadException {
        return SearchUtils.listJson(getClass().getSimpleName(), queries(request));
    }

    private SearchQueries queries(HttpServletRequest request) {
        boolean hasSearchTerm = isNotEmpty(extractSearchTerm(request));
        boolean upcoming = "upcoming".equals(request.getParameter("view"));//published releases are requested
        return () -> combine(
                buildListQuery(request, filters(request, upcoming), resolveSorting(hasSearchTerm, upcoming))
                        .types(ContentType.release)
        );
    }

    private SearchFilter filters(HttpServletRequest request, boolean upcoming) {
        return (query) -> {
            ONSFilterBuilders.filterDates(request, query);
            if (upcoming) {
                filterUpcoming(query);
            } else {//published
                filterPublished(query);
            }
        };
    }

    private void filterUpcoming(BoolQueryBuilder query) {
        QueryBuilder notPublishedNotCancelled = and(not(published()), not(cancelled()));
        QueryBuilder cancelledButNotDue = and(cancelled(), not(due()));
        query.filter(or(notPublishedNotCancelled, cancelledButNotDue));
    }

    private void filterPublished(BoolQueryBuilder query) {
        QueryBuilder publishedNotCancelled = and(published(), not(cancelled()));
        QueryBuilder cancelledAndDue = and(cancelled(), due());
        query.filter(or(publishedNotCancelled, cancelledAndDue));
    }

    private SortBy resolveSorting(boolean hasSearchTerm, boolean upcoming) {
        SortBy sortBy;
        if (hasSearchTerm) {
            sortBy = SortBy.relevance;
        } else {
            sortBy = upcoming ? SortBy.release_date_asc : SortBy.release_date;
        }
        return sortBy;
    }


    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }


    private QueryBuilder published() {
        return termQuery(Field.published.fieldName(), true);
    }

    private QueryBuilder cancelled() {
        return termQuery(Field.cancelled.fieldName(), true);
    }

    private QueryBuilder due() {
        return rangeQuery(Field.releaseDate.fieldName()).to(new Date());
    }

    private QueryBuilder not(QueryBuilder query) {
        return boolQuery().mustNot(query);
    }

    //Or query for given two queries. Database would help a lot , wouldn't it ?
    private QueryBuilder or(QueryBuilder q1, QueryBuilder q2) {
        return boolQuery().should(q1).should(q2);
    }

    private QueryBuilder and(QueryBuilder q1, QueryBuilder q2) {
        return boolQuery().must(q1).must(q2);
    }

}
