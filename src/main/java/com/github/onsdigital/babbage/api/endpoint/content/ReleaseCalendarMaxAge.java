package com.github.onsdigital.babbage.api.endpoint.content;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.index.query.QueryBuilders;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.util.SearchUtils;
import com.github.onsdigital.babbage.request.handler.list.ReleaseCalendar;
import com.github.onsdigital.babbage.search.builders.ONSQueryBuilders;
import com.github.onsdigital.babbage.search.helpers.ONSQuery;
import com.github.onsdigital.babbage.search.helpers.base.SearchFilter;
import com.github.onsdigital.babbage.search.input.SortBy;
import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;

/**
 * End point for getting the maxAge for the /releasecalendar
 */
@Api
public class ReleaseCalendarMaxAge extends MaxAge {
    private static final String QUERY_NAME = "nextRelease";

    @Override
    protected int getMaxAge(HttpServletRequest request) throws Exception {
        int maxAge = appConfig().babbage().getDefaultContentCacheTime();
        
        Instant nextReleaseDate = getNextReleaseDate();
        Instant now = Instant.now();

        if (nextReleaseDate != null && nextReleaseDate.isAfter(now)) {
            Long secondsUntilNextRelease = now.until(nextReleaseDate, ChronoUnit.SECONDS);
            if (secondsUntilNextRelease < maxAge) {
                maxAge = secondsUntilNextRelease.intValue();
            }
        }
        return maxAge;
    }

    @Override
    protected String getApiName() {
        return "ReleaseCalendarMaxAge";
    }

    protected Instant getNextReleaseDate() throws ParseException {
        Instant nextRelease = null;

        SearchFilter filter = ReleaseCalendar::filterUpcoming;
        ONSQuery query = ONSQueryBuilders.onsQuery(QueryBuilders.matchAllQuery(), filter)
                .name(QUERY_NAME)
                .types(ContentType.release)
                .fetchFields(Field.releaseDate)
                .size(1)
                .sortBy(SortBy.release_date_asc);
        Map<String, SearchResult> result = SearchUtils.searchAll(() -> ONSQueryBuilders.toList(query));

        if (result.get(QUERY_NAME).getResults().size() > 0) {
            // There will be just 1 result
            Map<String, String> description = ((Map<String, String>) result.get(QUERY_NAME).getResults().get(0).get("description"));
            nextRelease = Instant.parse(description.get("releaseDate"));
        }
        return nextRelease;
        
    }
}
