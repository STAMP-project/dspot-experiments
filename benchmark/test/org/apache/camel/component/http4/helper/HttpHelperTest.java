/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.http4.helper;


import HttpMethods.GET;
import HttpMethods.POST;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.http.common.HttpHelper;
import org.apache.camel.http.common.HttpMethods;
import org.junit.Assert;
import org.junit.Test;


public class HttpHelperTest {
    @Test
    public void testAppendHeader() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        HttpHelper.appendHeader(headers, "foo", "a");
        HttpHelper.appendHeader(headers, "bar", "b");
        HttpHelper.appendHeader(headers, "baz", "c");
        Assert.assertEquals(3, headers.size());
        Assert.assertEquals("a", headers.get("foo"));
        Assert.assertEquals("b", headers.get("bar"));
        Assert.assertEquals("c", headers.get("baz"));
    }

    @Test
    public void testAppendHeaderMultipleValues() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        HttpHelper.appendHeader(headers, "foo", "a");
        HttpHelper.appendHeader(headers, "bar", "b");
        HttpHelper.appendHeader(headers, "bar", "c");
        Assert.assertEquals(2, headers.size());
        Assert.assertEquals("a", headers.get("foo"));
        List<?> list = ((List<?>) (headers.get("bar")));
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("b", list.get(0));
        Assert.assertEquals("c", list.get(1));
    }

    @Test
    public void createURLShouldReturnTheHeaderURIIfNotBridgeEndpoint() throws URISyntaxException {
        String url = HttpHelper.createURL(createExchangeWithOptionalCamelHttpUriHeader("http://apache.org", null), createHttpEndpoint(false, "http://camel.apache.org"));
        Assert.assertEquals("http://apache.org", url);
    }

    @Test
    public void createURLShouldReturnTheEndpointURIIfBridgeEndpoint() throws URISyntaxException {
        String url = HttpHelper.createURL(createExchangeWithOptionalCamelHttpUriHeader("http://apache.org", null), createHttpEndpoint(true, "http://camel.apache.org"));
        Assert.assertEquals("http://camel.apache.org", url);
    }

    @Test
    public void createURLShouldReturnTheEndpointURIIfNotBridgeEndpoint() throws URISyntaxException {
        String url = HttpHelper.createURL(createExchangeWithOptionalCamelHttpUriHeader(null, null), createHttpEndpoint(false, "http://camel.apache.org"));
        Assert.assertEquals("http://camel.apache.org", url);
    }

    @Test
    public void createURLShouldReturnTheEndpointURIWithHeaderHttpPathAndAddOneSlash() throws URISyntaxException {
        String url = HttpHelper.createURL(createExchangeWithOptionalCamelHttpUriHeader(null, "search"), createHttpEndpoint(true, "http://www.google.com"));
        Assert.assertEquals("http://www.google.com/search", url);
    }

    @Test
    public void createURLShouldReturnTheEndpointURIWithHeaderHttpPathAndRemoveOneSlash() throws URISyntaxException {
        String url = HttpHelper.createURL(createExchangeWithOptionalCamelHttpUriHeader(null, "/search"), createHttpEndpoint(true, "http://www.google.com/"));
        Assert.assertEquals("http://www.google.com/search", url);
    }

    @Test
    public void createMethodAlwaysUseUserChoosenMethod() throws URISyntaxException {
        HttpMethods method = HttpHelper.createMethod(createExchangeWithOptionalHttpQueryAndHttpMethodHeader("q=camel", POST), createHttpEndpoint(true, "http://www.google.com/search"), false);
        Assert.assertEquals(POST, method);
    }

    @Test
    public void createMethodUseGETIfQueryIsProvidedInHeader() throws URISyntaxException {
        HttpMethods method = HttpHelper.createMethod(createExchangeWithOptionalHttpQueryAndHttpMethodHeader("q=camel", null), createHttpEndpoint(true, "http://www.google.com/search"), false);
        Assert.assertEquals(GET, method);
    }

    @Test
    public void createMethodUseGETIfQueryIsProvidedInEndpointURI() throws URISyntaxException {
        HttpMethods method = HttpHelper.createMethod(createExchangeWithOptionalHttpQueryAndHttpMethodHeader(null, null), createHttpEndpoint(true, "http://www.google.com/search?q=test"), false);
        Assert.assertEquals(GET, method);
    }

    @Test
    public void createMethodUseGETIfNoneQueryOrPayloadIsProvided() throws URISyntaxException {
        HttpMethods method = HttpHelper.createMethod(createExchangeWithOptionalHttpQueryAndHttpMethodHeader(null, null), createHttpEndpoint(true, "http://www.google.com/search"), false);
        Assert.assertEquals(GET, method);
    }

    @Test
    public void createMethodUsePOSTIfNoneQueryButPayloadIsProvided() throws URISyntaxException {
        HttpMethods method = HttpHelper.createMethod(createExchangeWithOptionalHttpQueryAndHttpMethodHeader(null, null), createHttpEndpoint(true, "http://www.google.com/search"), true);
        Assert.assertEquals(POST, method);
    }

    @Test
    public void createURLShouldNotRemoveTrailingSlash() throws Exception {
        String url = HttpHelper.createURL(createExchangeWithOptionalCamelHttpUriHeader(null, "/"), createHttpEndpoint(true, "http://www.google.com"));
        Assert.assertEquals("http://www.google.com/", url);
    }

    @Test
    public void createURLShouldAddPathAndQueryParamsAndSlash() throws Exception {
        String url = HttpHelper.createURL(createExchangeWithOptionalCamelHttpUriHeader(null, "search"), createHttpEndpoint(true, "http://www.google.com/context?test=true"));
        Assert.assertEquals("http://www.google.com/context/search?test=true", url);
    }

    @Test
    public void createURLShouldAddPathAndQueryParamsAndRemoveDuplicateSlash() throws Exception {
        String url = HttpHelper.createURL(createExchangeWithOptionalCamelHttpUriHeader(null, "/search"), createHttpEndpoint(true, "http://www.google.com/context/?test=true"));
        Assert.assertEquals("http://www.google.com/context/search?test=true", url);
    }

    @Test
    public void testIsStatusCodeOkSimpleRange() throws Exception {
        Assert.assertFalse(HttpHelper.isStatusCodeOk(199, "200-299"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(200, "200-299"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(299, "200-299"));
        Assert.assertFalse(HttpHelper.isStatusCodeOk(300, "200-299"));
        Assert.assertFalse(HttpHelper.isStatusCodeOk(300, "301-304"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(301, "301-304"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(304, "301-304"));
        Assert.assertFalse(HttpHelper.isStatusCodeOk(305, "301-304"));
    }

    @Test
    public void testIsStatusCodeOkComplexRange() throws Exception {
        Assert.assertFalse(HttpHelper.isStatusCodeOk(199, "200-299,404,301-304"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(200, "200-299,404,301-304"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(299, "200-299,404,301-304"));
        Assert.assertFalse(HttpHelper.isStatusCodeOk(300, "200-299,404,301-304"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(301, "200-299,404,301-304"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(304, "200-299,404,301-304"));
        Assert.assertFalse(HttpHelper.isStatusCodeOk(305, "200-299,404,301-304"));
        Assert.assertTrue(HttpHelper.isStatusCodeOk(404, "200-299,404,301-304"));
    }
}

