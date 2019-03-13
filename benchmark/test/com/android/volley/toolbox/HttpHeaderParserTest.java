/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.volley.toolbox;


import Cache.Entry;
import com.android.volley.Cache;
import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class HttpHeaderParserTest {
    private static long ONE_MINUTE_MILLIS = 1000L * 60;

    private static long ONE_HOUR_MILLIS = (1000L * 60) * 60;

    private static long ONE_DAY_MILLIS = (HttpHeaderParserTest.ONE_HOUR_MILLIS) * 24;

    private static long ONE_WEEK_MILLIS = (HttpHeaderParserTest.ONE_DAY_MILLIS) * 7;

    private NetworkResponse response;

    private Map<String, String> headers;

    @Test
    public void parseCacheHeaders_noHeaders() {
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNull(entry.etag);
        Assert.assertEquals(0, entry.serverDate);
        Assert.assertEquals(0, entry.lastModified);
        Assert.assertEquals(0, entry.ttl);
        Assert.assertEquals(0, entry.softTtl);
    }

    @Test
    public void parseCacheHeaders_headersSet() {
        headers.put("MyCustomHeader", "42");
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNotNull(entry.responseHeaders);
        Assert.assertEquals(1, entry.responseHeaders.size());
        Assert.assertEquals("42", entry.responseHeaders.get("MyCustomHeader"));
    }

    @Test
    public void parseCacheHeaders_etag() {
        headers.put("ETag", "Yow!");
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertEquals("Yow!", entry.etag);
    }

    @Test
    public void parseCacheHeaders_normalExpire() {
        long now = System.currentTimeMillis();
        headers.put("Date", HttpHeaderParserTest.rfc1123Date(now));
        headers.put("Last-Modified", HttpHeaderParserTest.rfc1123Date((now - (HttpHeaderParserTest.ONE_DAY_MILLIS))));
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNull(entry.etag);
        assertEqualsWithin(entry.serverDate, now, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        assertEqualsWithin(entry.lastModified, (now - (HttpHeaderParserTest.ONE_DAY_MILLIS)), HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        Assert.assertTrue(((entry.softTtl) >= (now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        Assert.assertTrue(((entry.ttl) == (entry.softTtl)));
    }

    @Test
    public void parseCacheHeaders_expiresInPast() {
        long now = System.currentTimeMillis();
        headers.put("Date", HttpHeaderParserTest.rfc1123Date(now));
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now - (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNull(entry.etag);
        assertEqualsWithin(entry.serverDate, now, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        Assert.assertEquals(0, entry.ttl);
        Assert.assertEquals(0, entry.softTtl);
    }

    @Test
    public void parseCacheHeaders_serverRelative() {
        long now = System.currentTimeMillis();
        // Set "current" date as one hour in the future
        headers.put("Date", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        // TTL four hours in the future, so should be three hours from now
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now + (4 * (HttpHeaderParserTest.ONE_HOUR_MILLIS)))));
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        assertEqualsWithin((now + (3 * (HttpHeaderParserTest.ONE_HOUR_MILLIS))), entry.ttl, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        Assert.assertEquals(entry.softTtl, entry.ttl);
    }

    @Test
    public void parseCacheHeaders_cacheControlOverridesExpires() {
        long now = System.currentTimeMillis();
        headers.put("Date", HttpHeaderParserTest.rfc1123Date(now));
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        headers.put("Cache-Control", "public, max-age=86400");
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNull(entry.etag);
        assertEqualsWithin((now + (HttpHeaderParserTest.ONE_DAY_MILLIS)), entry.ttl, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        Assert.assertEquals(entry.softTtl, entry.ttl);
    }

    @Test
    public void testParseCacheHeaders_staleWhileRevalidate() {
        long now = System.currentTimeMillis();
        headers.put("Date", HttpHeaderParserTest.rfc1123Date(now));
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        // - max-age (entry.softTtl) indicates that the asset is fresh for 1 day
        // - stale-while-revalidate (entry.ttl) indicates that the asset may
        // continue to be served stale for up to additional 7 days
        headers.put("Cache-Control", "max-age=86400, stale-while-revalidate=604800");
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNull(entry.etag);
        assertEqualsWithin((now + (HttpHeaderParserTest.ONE_DAY_MILLIS)), entry.softTtl, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        assertEqualsWithin(((now + (HttpHeaderParserTest.ONE_DAY_MILLIS)) + (HttpHeaderParserTest.ONE_WEEK_MILLIS)), entry.ttl, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
    }

    @Test
    public void parseCacheHeaders_cacheControlNoCache() {
        long now = System.currentTimeMillis();
        headers.put("Date", HttpHeaderParserTest.rfc1123Date(now));
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        headers.put("Cache-Control", "no-cache");
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNull(entry);
    }

    @Test
    public void parseCacheHeaders_cacheControlMustRevalidateNoMaxAge() {
        long now = System.currentTimeMillis();
        headers.put("Date", HttpHeaderParserTest.rfc1123Date(now));
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        headers.put("Cache-Control", "must-revalidate");
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNull(entry.etag);
        assertEqualsWithin(now, entry.ttl, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        Assert.assertEquals(entry.softTtl, entry.ttl);
    }

    @Test
    public void parseCacheHeaders_cacheControlMustRevalidateWithMaxAge() {
        long now = System.currentTimeMillis();
        headers.put("Date", HttpHeaderParserTest.rfc1123Date(now));
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        headers.put("Cache-Control", "must-revalidate, max-age=3600");
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNull(entry.etag);
        assertEqualsWithin((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS)), entry.ttl, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        Assert.assertEquals(entry.softTtl, entry.ttl);
    }

    @Test
    public void parseCacheHeaders_cacheControlMustRevalidateWithMaxAgeAndStale() {
        long now = System.currentTimeMillis();
        headers.put("Date", HttpHeaderParserTest.rfc1123Date(now));
        headers.put("Expires", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS))));
        // - max-age (entry.softTtl) indicates that the asset is fresh for 1 day
        // - stale-while-revalidate (entry.ttl) indicates that the asset may
        // continue to be served stale for up to additional 7 days, but this is
        // ignored in this case because of the must-revalidate header.
        headers.put("Cache-Control", "must-revalidate, max-age=86400, stale-while-revalidate=604800");
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertNull(entry.etag);
        assertEqualsWithin((now + (HttpHeaderParserTest.ONE_DAY_MILLIS)), entry.softTtl, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        Assert.assertEquals(entry.softTtl, entry.ttl);
    }

    // --------------------------
    @Test
    public void parseCharset() {
        // Like the ones we usually see
        headers.put("Content-Type", "text/plain; charset=utf-8");
        Assert.assertEquals("utf-8", HttpHeaderParser.parseCharset(headers));
        // Charset specified, ignore default charset
        headers.put("Content-Type", "text/plain; charset=utf-8");
        Assert.assertEquals("utf-8", HttpHeaderParser.parseCharset(headers, "ISO-8859-1"));
        // Extra whitespace
        headers.put("Content-Type", "text/plain;    charset=utf-8 ");
        Assert.assertEquals("utf-8", HttpHeaderParser.parseCharset(headers));
        // Extra parameters
        headers.put("Content-Type", "text/plain; charset=utf-8; frozzle=bar");
        Assert.assertEquals("utf-8", HttpHeaderParser.parseCharset(headers));
        // No Content-Type header
        headers.clear();
        Assert.assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));
        // No Content-Type header, use default charset
        headers.clear();
        Assert.assertEquals("utf-8", HttpHeaderParser.parseCharset(headers, "utf-8"));
        // Empty value
        headers.put("Content-Type", "text/plain; charset=");
        Assert.assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));
        // None specified
        headers.put("Content-Type", "text/plain");
        Assert.assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));
        // None charset specified, use default charset
        headers.put("Content-Type", "application/json");
        Assert.assertEquals("utf-8", HttpHeaderParser.parseCharset(headers, "utf-8"));
        // None specified, extra semicolon
        headers.put("Content-Type", "text/plain;");
        Assert.assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(headers));
    }

    @Test
    public void parseCaseInsensitive() {
        long now = System.currentTimeMillis();
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("eTAG", "Yow!"));
        headers.add(new Header("DATE", HttpHeaderParserTest.rfc1123Date(now)));
        headers.add(new Header("expires", HttpHeaderParserTest.rfc1123Date((now + (HttpHeaderParserTest.ONE_HOUR_MILLIS)))));
        headers.add(new Header("cache-control", "public, max-age=86400"));
        headers.add(new Header("content-type", "text/plain"));
        NetworkResponse response = new NetworkResponse(0, null, false, 0, headers);
        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        Assert.assertNotNull(entry);
        Assert.assertEquals("Yow!", entry.etag);
        assertEqualsWithin((now + (HttpHeaderParserTest.ONE_DAY_MILLIS)), entry.ttl, HttpHeaderParserTest.ONE_MINUTE_MILLIS);
        Assert.assertEquals(entry.softTtl, entry.ttl);
        Assert.assertEquals("ISO-8859-1", HttpHeaderParser.parseCharset(HttpHeaderParser.toHeaderMap(headers)));
    }
}

