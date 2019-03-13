/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import com.parse.http.ParseHttpResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ParseHttpResponseTest {
    @Test
    public void testParseHttpResponseDefaults() {
        ParseHttpResponse response = new ParseHttpResponse.Builder().build();
        Assert.assertNull(response.getContent());
        Assert.assertNull(response.getContentType());
        Assert.assertNull(response.getReasonPhrase());
        Assert.assertEquals(0, response.getStatusCode());
        Assert.assertEquals((-1), response.getTotalSize());
        Assert.assertEquals(0, response.getAllHeaders().size());
        Assert.assertNull(response.getHeader("test"));
    }

    @Test
    public void testParseHttpResponseGetMethod() throws IOException {
        Map<String, String> headers = new HashMap<>();
        String name = "name";
        String value = "value";
        headers.put(name, value);
        String content = "content";
        String contentType = "application/json";
        String reasonPhrase = "OK";
        int statusCode = 200;
        int totalSize = content.length();
        ParseHttpResponse response = setReasonPhrase(reasonPhrase).setStatusCode(statusCode).setTotalSize(totalSize).build();
        Assert.assertArrayEquals(content.getBytes(), ParseIOUtils.toByteArray(response.getContent()));
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(reasonPhrase, response.getReasonPhrase());
        Assert.assertEquals(statusCode, response.getStatusCode());
        Assert.assertEquals(totalSize, response.getTotalSize());
        Assert.assertEquals(value, response.getHeader(name));
        Assert.assertEquals(1, response.getAllHeaders().size());
    }

    @Test
    public void testParseHttpResponseBuildWithParseHttpResponse() {
        Map<String, String> headers = new HashMap<>();
        String name = "name";
        String value = "value";
        headers.put(name, value);
        String content = "content";
        String contentType = "application/json";
        String reasonPhrase = "OK";
        int statusCode = 200;
        int totalSize = content.length();
        ParseHttpResponse response = setReasonPhrase(reasonPhrase).setStatusCode(statusCode).setTotalSize(totalSize).build();
        String newReasonPhrase = "Failed";
        ParseHttpResponse newResponse = setReasonPhrase(newReasonPhrase).build();
        Assert.assertEquals(contentType, newResponse.getContentType());
        Assert.assertEquals(newReasonPhrase, newResponse.getReasonPhrase());
        Assert.assertEquals(statusCode, newResponse.getStatusCode());
        Assert.assertEquals(totalSize, newResponse.getTotalSize());
        Assert.assertEquals(value, newResponse.getHeader(name));
        Assert.assertEquals(1, newResponse.getAllHeaders().size());
        Assert.assertSame(response.getContent(), newResponse.getContent());
    }
}

