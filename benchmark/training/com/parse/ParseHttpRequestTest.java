/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseHttpRequest.Method;
import com.parse.http.ParseHttpBody;
import com.parse.http.ParseHttpRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ParseHttpRequestTest {
    @Test
    public void testParseHttpRequestGetMethod() throws IOException {
        String url = "www.parse.com";
        ParseHttpRequest.Method method = Method.POST;
        Map<String, String> headers = new HashMap<>();
        String name = "name";
        String value = "value";
        headers.put(name, value);
        String content = "content";
        String contentType = "application/json";
        ParseByteArrayHttpBody body = new ParseByteArrayHttpBody(content, contentType);
        ParseHttpRequest request = build();
        Assert.assertEquals(url, request.getUrl());
        Assert.assertEquals(method.toString(), request.getMethod().toString());
        Assert.assertEquals(1, request.getAllHeaders().size());
        Assert.assertEquals(value, request.getHeader(name));
        ParseHttpBody bodyAgain = request.getBody();
        Assert.assertEquals(contentType, bodyAgain.getContentType());
        Assert.assertArrayEquals(content.getBytes(), ParseIOUtils.toByteArray(body.getContent()));
    }

    @Test
    public void testParseHttpRequestBuilderInitialization() throws IOException {
        String url = "www.parse.com";
        ParseHttpRequest.Method method = Method.POST;
        Map<String, String> headers = new HashMap<>();
        String name = "name";
        String value = "value";
        headers.put(name, value);
        String content = "content";
        String contentType = "application/json";
        ParseByteArrayHttpBody body = new ParseByteArrayHttpBody(content, contentType);
        ParseHttpRequest request = build();
        ParseHttpRequest requestAgain = build();
        Assert.assertEquals(url, requestAgain.getUrl());
        Assert.assertEquals(method.toString(), requestAgain.getMethod().toString());
        Assert.assertEquals(1, requestAgain.getAllHeaders().size());
        Assert.assertEquals(value, requestAgain.getHeader(name));
        ParseHttpBody bodyAgain = requestAgain.getBody();
        Assert.assertEquals(contentType, bodyAgain.getContentType());
        Assert.assertArrayEquals(content.getBytes(), ParseIOUtils.toByteArray(body.getContent()));
    }

    @Test
    public void testParseHttpRequestBuildWithParseHttpRequest() throws IOException {
        String url = "www.parse.com";
        ParseHttpRequest.Method method = Method.POST;
        Map<String, String> headers = new HashMap<>();
        String name = "name";
        String value = "value";
        headers.put(name, value);
        String content = "content";
        String contentType = "application/json";
        ParseByteArrayHttpBody body = new ParseByteArrayHttpBody(content, contentType);
        ParseHttpRequest request = build();
        String newURL = "www.api.parse.com";
        ParseHttpRequest newRequest = build();
        Assert.assertEquals(newURL, newRequest.getUrl());
        Assert.assertEquals(method.toString(), newRequest.getMethod().toString());
        Assert.assertEquals(1, newRequest.getAllHeaders().size());
        Assert.assertEquals(value, newRequest.getHeader(name));
        ParseHttpBody bodyAgain = newRequest.getBody();
        Assert.assertEquals(contentType, bodyAgain.getContentType());
        Assert.assertArrayEquals(content.getBytes(), ParseIOUtils.toByteArray(body.getContent()));
    }
}

