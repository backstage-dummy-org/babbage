package com.github.onsdigital.babbage.response.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.info;

/**
 */
public class CacheControlHelper {

    /**
     * Resolves and sets response status based on request cache control headers and data to be sent to the user
     *
     * @param request
     * @return
     */
    public static void setCacheHeaders(HttpServletRequest request, HttpServletResponse response, String hash, long maxAge) {
        resolveHash(request, response, hash);
        setMaxAage(response, maxAge);
    }

    public static void setCacheHeaders(HttpServletResponse response, long maxAge) {
        setMaxAage(response, maxAge);
    }

    private static void setMaxAage(HttpServletResponse response, long maxAge) {
        String cacheHeaderVal = "public, max-age=" + maxAge;
        info().log("setting cache-control header to: " + cacheHeaderVal);
        response.addHeader("cache-control", cacheHeaderVal);
    }

    public static String hashData(String data) {
        return DigestUtils.sha1Hex(data);
    }

    public static String hashData(byte[] data) {
        return DigestUtils.sha1Hex(data);
    }

    private static void resolveHash(HttpServletRequest request, HttpServletResponse response, String newHash) {
        if (StringUtils.isEmpty(newHash)) {
            return;
        }
        String oldHash = getOldHash(request);

        info().data("old_hash", oldHash)
                .data("new_hash", newHash)
                .log("resolving cache headers");

        response.setHeader("Etag", newHash);
        if (StringUtils.equals(oldHash, newHash)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    private static String getOldHash(HttpServletRequest request) {
        String hash = request.getHeader("If-None-Match");
        return StringUtils.remove(hash, "--gzip");//TODO: Restolino does not seem to be removing --gzip flag on etag when request comes in
    }
}
