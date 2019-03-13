/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.adapters.springsecurity.facade;


import RequestMethod.GET;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Wrapped HTTP servlet request tests.
 */
public class WrappedHttpServletRequestTest {
    private static final String COOKIE_NAME = "oreo";

    private static final String HEADER_MULTI_VALUE = "Multi";

    private static final String HEADER_SINGLE_VALUE = "Single";

    private static final String REQUEST_METHOD = GET.name();

    private static final String REQUEST_URI = "/foo/bar";

    private static final String QUERY_PARM_1 = "code";

    private static final String QUERY_PARM_2 = "code2";

    private WrappedHttpServletRequest request;

    private MockHttpServletRequest mockHttpServletRequest;

    @Test
    public void testGetMethod() throws Exception {
        Assert.assertNotNull(request.getMethod());
        Assert.assertEquals(WrappedHttpServletRequestTest.REQUEST_METHOD, request.getMethod());
    }

    @Test
    public void testGetURI() throws Exception {
        Assert.assertEquals((("https://localhost:80" + (WrappedHttpServletRequestTest.REQUEST_URI)) + "?code=java&code2=groovy"), request.getURI());
    }

    @Test
    public void testIsSecure() throws Exception {
        Assert.assertTrue(request.isSecure());
    }

    @Test
    public void testGetQueryParamValue() throws Exception {
        Assert.assertNotNull(request.getQueryParamValue(WrappedHttpServletRequestTest.QUERY_PARM_1));
        Assert.assertNotNull(request.getQueryParamValue(WrappedHttpServletRequestTest.QUERY_PARM_2));
    }

    @Test
    public void testGetCookie() throws Exception {
        Assert.assertNotNull(request.getCookie(WrappedHttpServletRequestTest.COOKIE_NAME));
    }

    @Test
    public void testGetCookieCookiesNull() throws Exception {
        mockHttpServletRequest.setCookies(null);
        request.getCookie(WrappedHttpServletRequestTest.COOKIE_NAME);
    }

    @Test
    public void testGetHeader() throws Exception {
        String header = request.getHeader(WrappedHttpServletRequestTest.HEADER_SINGLE_VALUE);
        Assert.assertNotNull(header);
        Assert.assertEquals("baz", header);
    }

    @Test
    public void testGetHeaders() throws Exception {
        List<String> headers = request.getHeaders(WrappedHttpServletRequestTest.HEADER_MULTI_VALUE);
        Assert.assertNotNull(headers);
        Assert.assertEquals(2, headers.size());
        Assert.assertTrue(headers.contains("foo"));
        Assert.assertTrue(headers.contains("bar"));
    }

    @Test
    public void testGetInputStream() throws Exception {
        Assert.assertNotNull(request.getInputStream());
    }

    @Test
    public void testGetRemoteAddr() throws Exception {
        Assert.assertNotNull(request.getRemoteAddr());
    }
}

