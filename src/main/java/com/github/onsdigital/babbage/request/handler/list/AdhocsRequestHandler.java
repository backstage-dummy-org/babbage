package com.github.onsdigital.babbage.request.handler.list;

import com.github.onsdigital.babbage.search.model.ContentType;
import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;

/**
 * Render a list page for bulletins under the given URI.
 */
public class AdhocsRequestHandler extends ListPageBaseRequestHandler {

    private final static String REQEUST_TYPE = "adhoc";
    private final static ContentType[] ALLOWED_TYPES = {ContentType.static_adhoc};

    @Override
    public String getRequestType() {
        return REQEUST_TYPE;
    }

    @Override
    public ContentType[] getAllowedTypes() {
        return ALLOWED_TYPES;
    }

    @Override
    public boolean useLocalisedUri() {
        return true;
    }
}
