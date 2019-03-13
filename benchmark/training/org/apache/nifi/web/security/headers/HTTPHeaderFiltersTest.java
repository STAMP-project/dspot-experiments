/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.security.headers;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.servlet.FilterHolder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;


public class HTTPHeaderFiltersTest {
    @Test
    public void testCSPHeaderApplied() throws IOException, ServletException {
        // Arrange
        FilterHolder originFilter = new FilterHolder(new ContentSecurityPolicyFilter());
        // Set up request
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);
        // Action
        originFilter.getFilter().doFilter(mockRequest, mockResponse, mockFilterChain);
        // Verify
        Assert.assertEquals("frame-ancestors 'self'", mockResponse.getHeader("Content-Security-Policy"));
    }

    @Test
    public void testCSPHeaderAppliedOnlyOnce() throws IOException, ServletException {
        // Arrange
        FilterHolder originFilter = new FilterHolder(new ContentSecurityPolicyFilter());
        // Set up request
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);
        // Action
        originFilter.getFilter().doFilter(mockRequest, mockResponse, mockFilterChain);
        originFilter.getFilter().doFilter(mockRequest, mockResponse, mockFilterChain);
        // Verify
        Assert.assertEquals("frame-ancestors 'self'", mockResponse.getHeader("Content-Security-Policy"));
    }

    @Test
    public void testXFrameOptionsHeaderApplied() throws IOException, ServletException {
        // Arrange
        FilterHolder xfoFilter = new FilterHolder(new XFrameOptionsFilter());
        // Set up request
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);
        // Action
        xfoFilter.getFilter().doFilter(mockRequest, mockResponse, mockFilterChain);
        // Verify
        Assert.assertEquals("SAMEORIGIN", mockResponse.getHeader("X-Frame-Options"));
    }

    @Test
    public void testHSTSHeaderApplied() throws IOException, ServletException {
        // Arrange
        FilterHolder hstsFilter = new FilterHolder(new StrictTransportSecurityFilter());
        // Set up request
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);
        // Action
        hstsFilter.getFilter().doFilter(mockRequest, mockResponse, mockFilterChain);
        // Verify
        Assert.assertEquals("max-age=31540000", mockResponse.getHeader("Strict-Transport-Security"));
    }

    @Test
    public void testXSSProtectionHeaderApplied() throws IOException, ServletException {
        // Arrange
        FilterHolder xssFilter = new FilterHolder(new XSSProtectionFilter());
        // Set up request
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);
        // Action
        xssFilter.getFilter().doFilter(mockRequest, mockResponse, mockFilterChain);
        // Verify
        Assert.assertEquals("1; mode=block", mockResponse.getHeader("X-XSS-Protection"));
    }
}

