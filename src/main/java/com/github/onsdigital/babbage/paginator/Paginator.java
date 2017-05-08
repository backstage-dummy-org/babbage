package com.github.onsdigital.babbage.paginator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bren on 08/09/15.
 * <p>
 * Paginator model to be included in search and list data results for server side rendering
 */
public class Paginator {

    private long numberOfPages;
    private long currentPage;
    private long start;
    private long end;
    private List<Long> pages;

    public Paginator(long numberOfResults, int maxVisibleLinks, long currentPage, int resultPerPage) {
        this.currentPage = currentPage;
        this.numberOfPages = calculateNumberOfPages(numberOfResults, resultPerPage);
        this.end = calculateEnd(numberOfPages, currentPage, maxVisibleLinks);
        this.start = calculateStart(numberOfPages, maxVisibleLinks, end);
        this.pages = getPageList(start, end);
    }

    private long calculateNumberOfPages(long numberOfResults, int resultsPerPage) {
        return (long) Math.ceil((double) numberOfResults / resultsPerPage);
    }

    private long calculateEnd(long numberOfPages, long currentPage, int maxVisible) {
        long max = numberOfPages;
        if (max <= maxVisible) {
            return max;
        }
        //Half of the pages are visible after current page
        long end = (long) (currentPage + Math.ceil(maxVisible / 2));
        end = (end > max) ? max : end;
        end = (end < maxVisible) ? maxVisible : end;
        return end;
    }


    private long calculateStart(long numberOfPages, int maxVisible, long end) {
        if (numberOfPages <= maxVisible) {
            return 1;
        }
        long start = end - maxVisible + 1;
        start = start > 0 ? start : 1;
        return start;
    }

    private java.util.List<Long> getPageList(long start, long end) {
        ArrayList<Long> pageList = new ArrayList<Long>();
        for (long i = start; i <= end; i++) {
            pageList.add(i);
        }
        return pageList;
    }

    public List<Long> getPages() {
        return pages;
    }

    public void setPages(List<Long> pages) {
        this.pages = pages;
    }


    public long getNumberOfPages() {
        return numberOfPages;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }


}
