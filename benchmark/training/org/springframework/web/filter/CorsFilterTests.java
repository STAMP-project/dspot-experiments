/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.web.filter;


import HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.OPTIONS;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;


/**
 * Unit tests for {@link CorsFilter}.
 *
 * @author Sebastien Deleuze
 */
public class CorsFilterTests {
    private CorsFilter filter;

    private final CorsConfiguration config = new CorsConfiguration();

    @Test
    public void validActualRequest() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(GET.name(), "/test.html");
        request.addHeader(ORIGIN, "http://domain2.com");
        request.addHeader("header2", "foo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = ( filterRequest, filterResponse) -> {
            assertEquals("http://domain2.com", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
            assertEquals("header3, header4", response.getHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS));
        };
        filter.doFilter(request, response, filterChain);
    }

    @Test
    public void invalidActualRequest() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(DELETE.name(), "/test.html");
        request.addHeader(ORIGIN, "http://domain2.com");
        request.addHeader("header2", "foo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = ( filterRequest, filterResponse) -> {
            fail("Invalid requests must not be forwarded to the filter chain");
        };
        filter.doFilter(request, response, filterChain);
        Assert.assertNull(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void validPreFlightRequest() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(OPTIONS.name(), "/test.html");
        request.addHeader(ORIGIN, "http://domain2.com");
        request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, GET.name());
        request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "header1, header2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = ( filterRequest, filterResponse) -> fail("Preflight requests must not be forwarded to the filter chain");
        filter.doFilter(request, response, filterChain);
        Assert.assertEquals("http://domain2.com", response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertEquals("header1, header2", response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS));
        Assert.assertEquals("header3, header4", response.getHeader(ACCESS_CONTROL_EXPOSE_HEADERS));
        Assert.assertEquals(123L, Long.parseLong(response.getHeader(ACCESS_CONTROL_MAX_AGE)));
    }

    @Test
    public void invalidPreFlightRequest() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(OPTIONS.name(), "/test.html");
        request.addHeader(ORIGIN, "http://domain2.com");
        request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, DELETE.name());
        request.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "header1, header2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = ( filterRequest, filterResponse) -> fail("Preflight requests must not be forwarded to the filter chain");
        filter.doFilter(request, response, filterChain);
        Assert.assertNull(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}

