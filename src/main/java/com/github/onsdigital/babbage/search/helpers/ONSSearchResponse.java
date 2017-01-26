package com.github.onsdigital.babbage.search.helpers;

import com.github.onsdigital.babbage.search.model.SearchResult;
import com.github.onsdigital.babbage.search.model.field.Field;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bren on 07/09/15.
 */
public class ONSSearchResponse {

    SearchResponse response;
    SearchResult result;

    public ONSSearchResponse(SearchResponse response) {
        this.response = response;
        this.result = buildResult();
    }

    public long getNumberOfResults() {
        return response.getHits()
                       .getTotalHits();
    }

    public SearchResult getResult() {
        return this.result;
    }

    private SearchResult buildResult() {
        SearchResult searchResult = new SearchResult();
        searchResult.setNumberOfResults(getNumberOfResults());
        searchResult.setTook(response.getTookInMillis());

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> source = extractSource(hit);
            searchResult.addResult(source);
        }

        extractDocCounts(searchResult);
        extractSuggestions(searchResult);

        return searchResult;
    }

    private void extractSuggestions(SearchResult searchResult) {
        Suggest suggest = response.getSuggest();
        if (suggest == null) {
            return;
        }
        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> suggestion = suggest.getSuggestion(
                "search_suggest");
        if (suggestion == null) {
            return;
        }


        List<String> suggestions = new ArrayList<>();

        Iterator<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> iterator = suggestion.getEntries()
                                                                                                                     .iterator();
        while (iterator.hasNext()) {
            Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry = iterator.next();
            suggestions.addAll(entry.getOptions()
                                    .stream()
                                    .map(option -> option.getText()
                                                         .string())
                                    .collect(Collectors.toList()));
        }
        if (!suggestions.isEmpty()) {
            searchResult.setSuggestions(suggestions);
        }
    }

    private void extractDocCounts(SearchResult searchResult) {
        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            addCounts(searchResult, aggregations);
        }
    }


    /*
    Flattens and adds bucket results recursively to result as key value pairs
     */
    private void addCounts(SearchResult searchResult, Aggregations aggregations) {
        for (Aggregation aggregation : aggregations) {
            if (aggregation instanceof MultiBucketsAggregation) {
                for (MultiBucketsAggregation.Bucket bucket : ((MultiBucketsAggregation) aggregation).getBuckets()) {
                    searchResult.addDocCount(bucket.getKeyAsString(), bucket.getDocCount());
                }
            }
            else {
                addCounts(searchResult, ((SingleBucketAggregation) aggregation).getAggregations());
            }
        }
    }

    private Map<String, Object> extractSource(SearchHit hit) {
        Map<String, Object> source = new HashMap<>(hit.getSource());
        source.put("_type", hit.getType());
        Map<String, HighlightField> highlightFields = new HashMap<>(hit.getHighlightFields());
        overlayHighlightFields(source, highlightFields);
        addExcludedHighlightedFields(source, highlightFields);
        return source;
    }

    /**
     * As the source of the content of the download files could be huge it is excluded from the "_source" requested
     * from ES, therefore we need to build up the 'highlights' purely from the highlights themselves
     * @param source
     * @param highlightFields
     */
    private void addExcludedHighlightedFields(final Map<String, Object> source,
                                              final Map<String, HighlightField> highlightFields) {

        Arrays.stream(Field.excludedSource())
              .map(src -> src.fieldName())
              .filter(f -> null != highlightFields.get(f))
              .forEach(fieldName -> {
                  String joined = StringUtils.join(highlightFields.get(fieldName)
                                                                  .getFragments(), "...");
                  // the Moustache front end appears to treat periods "." as a nested object and this is not how the document is represented from ES
                  // therefore flattern' by substituting with underscores
                  source.put(fieldName.replace(".", "_"), joined);
              });
    }


    private void overlayHighlightFields(Map<String, Object> source, Map<String, HighlightField> highlightedFields) {
        if (highlightedFields.isEmpty()) {
            return;
        }


        ArrayList<Map<String, Object>> nestedObjects = new ArrayList<>();
        for (Map.Entry<String, Object> sourceEntry : source.entrySet()) {
            Object field = sourceEntry.getValue();
            if (saveIfNested(nestedObjects, field)) {
                continue;
            }
            else if (field instanceof Collection) {
                for (Object o : (Collection) field) {
                    saveIfNested(nestedObjects, o);
                }
            }
            HighlightField highlightedField = highlightedFields.remove(getFieldName(sourceEntry.getKey()));
            if (highlightedField == null) {//not found
                continue;
            }
            else {
                overlay(sourceEntry, field, highlightedField);
            }
        }

        for (Map<String, Object> nestedObject : nestedObjects) {
            overlayHighlightFields(nestedObject, highlightedFields);
        }


    }

    private void overlay(Map.Entry<String, Object> sourceEntry, Object field, HighlightField highlightedField) {
        Text[] fragments = highlightedField.getFragments();
        if (field instanceof Collection) {
            Collection arrayField = (Collection) field;
            for (Text fragment : fragments) {
                String fragmentString = fragment.toString();
                String noTag = removeHighlightTags(fragmentString);
                arrayField.remove(noTag);//removes existing non-highlighted text
                arrayField.add(fragmentString);
            }
        }
        else {
            sourceEntry.setValue(StringUtils.join(fragments, ","));
        }
        return;
    }

    private String removeHighlightTags(String fragment) {
        String s = fragment;
        s = s.replaceAll("<strong>", "");
        s = s.replaceAll("</strong>", "");
        return s;
    }

    private boolean saveIfNested(ArrayList list, Object field) {
        if (field instanceof Map) {
            list.add(field);
            return true;
        }
        return false;
    }

    private String getFieldName(String fieldKey) {
        try {
            return Field.valueOf(fieldKey)
                        .fieldName();
        }
        catch (IllegalArgumentException e) {
            return fieldKey;
        }
    }

}
