/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.logging;


import RequestMethod.POST;
import com.google.cloud.logging.HttpRequest.RequestMethod;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.threeten.bp.Duration;


public class HttpRequestTest {
    private static final RequestMethod REQUEST_METHOD = RequestMethod.GET;

    private static final String REQUEST_URL = "http://www.example.com";

    private static final Long REQUEST_SIZE = 1L;

    private static final Integer STATUS = 200;

    private static final Long REPONSE_SIZE = 2L;

    private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; Q312461; .NET CLR 1.0.3705)";

    private static final String REMOTE_IP = "192.168.1.1";

    private static final String SERVER_IP = "192.168.1.2";

    private static final String REFERER = "Referer: http://www.example.com";

    private static final boolean CACHE_LOOKUP = true;

    private static final boolean CACHE_HIT = true;

    private static final boolean CACHE_VALIDATED_WITH_ORIGIN_SERVER = false;

    private static final Long CACHE_FILL_BYTES = 3L;

    private static final HttpRequest HTTP_REQUEST = HttpRequest.newBuilder().setRequestMethod(HttpRequestTest.REQUEST_METHOD).setRequestUrl(HttpRequestTest.REQUEST_URL).setRequestSize(HttpRequestTest.REQUEST_SIZE).setStatus(HttpRequestTest.STATUS).setResponseSize(HttpRequestTest.REPONSE_SIZE).setUserAgent(HttpRequestTest.USER_AGENT).setRemoteIp(HttpRequestTest.REMOTE_IP).setServerIp(HttpRequestTest.SERVER_IP).setReferer(HttpRequestTest.REFERER).setCacheLookup(HttpRequestTest.CACHE_LOOKUP).setCacheHit(HttpRequestTest.CACHE_HIT).setCacheValidatedWithOriginServer(HttpRequestTest.CACHE_VALIDATED_WITH_ORIGIN_SERVER).setCacheFillBytes(HttpRequestTest.CACHE_FILL_BYTES).setLatency(Duration.ofSeconds(123, 456)).build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBuilder() {
        Assert.assertEquals(HttpRequestTest.REQUEST_METHOD, HttpRequestTest.HTTP_REQUEST.getRequestMethod());
        Assert.assertEquals(HttpRequestTest.REQUEST_URL, HttpRequestTest.HTTP_REQUEST.getRequestUrl());
        Assert.assertEquals(HttpRequestTest.REQUEST_SIZE, HttpRequestTest.HTTP_REQUEST.getRequestSize());
        Assert.assertEquals(HttpRequestTest.STATUS, HttpRequestTest.HTTP_REQUEST.getStatus());
        Assert.assertEquals(HttpRequestTest.REPONSE_SIZE, HttpRequestTest.HTTP_REQUEST.getResponseSize());
        Assert.assertEquals(HttpRequestTest.USER_AGENT, HttpRequestTest.HTTP_REQUEST.getUserAgent());
        Assert.assertEquals(HttpRequestTest.REMOTE_IP, HttpRequestTest.HTTP_REQUEST.getRemoteIp());
        Assert.assertEquals(HttpRequestTest.SERVER_IP, HttpRequestTest.HTTP_REQUEST.getServerIp());
        Assert.assertEquals(HttpRequestTest.REFERER, HttpRequestTest.HTTP_REQUEST.getReferer());
        Assert.assertEquals(HttpRequestTest.CACHE_LOOKUP, HttpRequestTest.HTTP_REQUEST.cacheLookup());
        Assert.assertEquals(HttpRequestTest.CACHE_HIT, HttpRequestTest.HTTP_REQUEST.cacheHit());
        Assert.assertEquals(HttpRequestTest.CACHE_VALIDATED_WITH_ORIGIN_SERVER, HttpRequestTest.HTTP_REQUEST.cacheValidatedWithOriginServer());
        Assert.assertEquals(HttpRequestTest.CACHE_FILL_BYTES, HttpRequestTest.HTTP_REQUEST.getCacheFillBytes());
    }

    @Test
    public void testBuilderDefaultValues() {
        HttpRequest httpRequest = HttpRequest.newBuilder().build();
        Assert.assertNull(httpRequest.getRequestMethod());
        Assert.assertNull(httpRequest.getRequestUrl());
        Assert.assertNull(httpRequest.getRequestSize());
        Assert.assertNull(httpRequest.getStatus());
        Assert.assertNull(httpRequest.getResponseSize());
        Assert.assertNull(httpRequest.getUserAgent());
        Assert.assertNull(httpRequest.getRemoteIp());
        Assert.assertNull(httpRequest.getServerIp());
        Assert.assertNull(httpRequest.getReferer());
        Assert.assertFalse(httpRequest.cacheLookup());
        Assert.assertFalse(httpRequest.cacheHit());
        Assert.assertFalse(httpRequest.cacheValidatedWithOriginServer());
        Assert.assertNull(httpRequest.getCacheFillBytes());
    }

    @Test
    public void testToBuilder() {
        compareHttpRequest(HttpRequestTest.HTTP_REQUEST, HttpRequestTest.HTTP_REQUEST.toBuilder().build());
        HttpRequest httpRequest = HttpRequestTest.HTTP_REQUEST.toBuilder().setRequestMethod(POST).setRequestUrl("http://www.other-example.com").setRequestSize(4).setStatus(201).setResponseSize(5).setUserAgent("otherUserAgent").setRemoteIp("192.168.1.3").setServerIp("192.168.1.4").setReferer("Referer: http://www.other-example.com").setCacheLookup(true).setCacheHit(true).setCacheValidatedWithOriginServer(true).setCacheFillBytes(6).build();
        Assert.assertEquals(POST, httpRequest.getRequestMethod());
        Assert.assertEquals("http://www.other-example.com", httpRequest.getRequestUrl());
        Assert.assertEquals(4, ((long) (httpRequest.getRequestSize())));
        Assert.assertEquals(201, ((int) (httpRequest.getStatus())));
        Assert.assertEquals(5, ((long) (httpRequest.getResponseSize())));
        Assert.assertEquals("otherUserAgent", httpRequest.getUserAgent());
        Assert.assertEquals("192.168.1.3", httpRequest.getRemoteIp());
        Assert.assertEquals("192.168.1.4", httpRequest.getServerIp());
        Assert.assertEquals("Referer: http://www.other-example.com", httpRequest.getReferer());
        Assert.assertTrue(httpRequest.cacheLookup());
        Assert.assertTrue(httpRequest.cacheHit());
        Assert.assertTrue(httpRequest.cacheValidatedWithOriginServer());
        Assert.assertEquals(6, ((long) (httpRequest.getCacheFillBytes())));
    }

    @Test
    public void testToAndFromPb() {
        HttpRequest httpRequest = HttpRequest.fromPb(HttpRequestTest.HTTP_REQUEST.toPb());
        compareHttpRequest(HttpRequestTest.HTTP_REQUEST, httpRequest);
        Assert.assertEquals(HttpRequestTest.REQUEST_METHOD, httpRequest.getRequestMethod());
        Assert.assertEquals(HttpRequestTest.REQUEST_URL, httpRequest.getRequestUrl());
        Assert.assertEquals(HttpRequestTest.REQUEST_SIZE, httpRequest.getRequestSize());
        Assert.assertEquals(HttpRequestTest.STATUS, httpRequest.getStatus());
        Assert.assertEquals(HttpRequestTest.REPONSE_SIZE, httpRequest.getResponseSize());
        Assert.assertEquals(HttpRequestTest.USER_AGENT, httpRequest.getUserAgent());
        Assert.assertEquals(HttpRequestTest.REMOTE_IP, httpRequest.getRemoteIp());
        Assert.assertEquals(HttpRequestTest.SERVER_IP, httpRequest.getServerIp());
        Assert.assertEquals(HttpRequestTest.REFERER, httpRequest.getReferer());
        Assert.assertEquals(HttpRequestTest.CACHE_LOOKUP, httpRequest.cacheLookup());
        Assert.assertEquals(HttpRequestTest.CACHE_HIT, httpRequest.cacheHit());
        Assert.assertEquals(HttpRequestTest.CACHE_VALIDATED_WITH_ORIGIN_SERVER, httpRequest.cacheValidatedWithOriginServer());
        Assert.assertEquals(HttpRequestTest.CACHE_FILL_BYTES, httpRequest.getCacheFillBytes());
        HttpRequest incompleteHttpRequest = HttpRequest.newBuilder().build();
        httpRequest = HttpRequest.fromPb(incompleteHttpRequest.toPb());
        compareHttpRequest(incompleteHttpRequest, httpRequest);
        Assert.assertNull(httpRequest.getRequestMethod());
        Assert.assertNull(httpRequest.getRequestUrl());
        Assert.assertNull(httpRequest.getRequestSize());
        Assert.assertNull(httpRequest.getStatus());
        Assert.assertNull(httpRequest.getResponseSize());
        Assert.assertNull(httpRequest.getUserAgent());
        Assert.assertNull(httpRequest.getRemoteIp());
        Assert.assertNull(httpRequest.getServerIp());
        Assert.assertNull(httpRequest.getReferer());
        Assert.assertFalse(httpRequest.cacheLookup());
        Assert.assertFalse(httpRequest.cacheHit());
        Assert.assertFalse(httpRequest.cacheValidatedWithOriginServer());
        Assert.assertNull(httpRequest.getCacheFillBytes());
    }
}

