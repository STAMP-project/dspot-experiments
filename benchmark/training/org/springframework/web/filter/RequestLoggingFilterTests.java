/**
 * Copyright 2002-2017 the original author or authors.
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


import HttpServletResponse.SC_OK;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;


/**
 * Test for {@link AbstractRequestLoggingFilter} and subclasses.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 */
public class RequestLoggingFilterTests {
    private final RequestLoggingFilterTests.MyRequestLoggingFilter filter = new RequestLoggingFilterTests.MyRequestLoggingFilter();

    @Test
    public void uri() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/hotels");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setQueryString("booking=42");
        FilterChain filterChain = new RequestLoggingFilterTests.NoOpFilterChain();
        filter.doFilter(request, response, filterChain);
        Assert.assertNotNull(filter.beforeRequestMessage);
        Assert.assertTrue(filter.beforeRequestMessage.contains("uri=/hotel"));
        Assert.assertFalse(filter.beforeRequestMessage.contains("booking=42"));
        Assert.assertNotNull(filter.afterRequestMessage);
        Assert.assertTrue(filter.afterRequestMessage.contains("uri=/hotel"));
        Assert.assertFalse(filter.afterRequestMessage.contains("booking=42"));
    }

    @Test
    public void queryStringIncluded() throws Exception {
        setIncludeQueryString(true);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/hotels");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setQueryString("booking=42");
        FilterChain filterChain = new RequestLoggingFilterTests.NoOpFilterChain();
        filter.doFilter(request, response, filterChain);
        Assert.assertNotNull(filter.beforeRequestMessage);
        Assert.assertTrue(filter.beforeRequestMessage.contains("[uri=/hotels?booking=42]"));
        Assert.assertNotNull(filter.afterRequestMessage);
        Assert.assertTrue(filter.afterRequestMessage.contains("[uri=/hotels?booking=42]"));
    }

    @Test
    public void noQueryStringAvailable() throws Exception {
        setIncludeQueryString(true);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/hotels");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new RequestLoggingFilterTests.NoOpFilterChain();
        filter.doFilter(request, response, filterChain);
        Assert.assertNotNull(filter.beforeRequestMessage);
        Assert.assertTrue(filter.beforeRequestMessage.contains("[uri=/hotels]"));
        Assert.assertNotNull(filter.afterRequestMessage);
        Assert.assertTrue(filter.afterRequestMessage.contains("[uri=/hotels]"));
    }

    @Test
    public void payloadInputStream() throws Exception {
        setIncludePayload(true);
        final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/hotels");
        MockHttpServletResponse response = new MockHttpServletResponse();
        final byte[] requestBody = "Hello World".getBytes("UTF-8");
        request.setContent(requestBody);
        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest filterRequest, ServletResponse filterResponse) throws IOException, ServletException {
                ((HttpServletResponse) (filterResponse)).setStatus(SC_OK);
                byte[] buf = FileCopyUtils.copyToByteArray(filterRequest.getInputStream());
                Assert.assertArrayEquals(requestBody, buf);
            }
        };
        filter.doFilter(request, response, filterChain);
        Assert.assertNotNull(filter.afterRequestMessage);
        Assert.assertTrue(filter.afterRequestMessage.contains("Hello World"));
    }

    @Test
    public void payloadReader() throws Exception {
        setIncludePayload(true);
        final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/hotels");
        MockHttpServletResponse response = new MockHttpServletResponse();
        final String requestBody = "Hello World";
        request.setContent(requestBody.getBytes("UTF-8"));
        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest filterRequest, ServletResponse filterResponse) throws IOException, ServletException {
                ((HttpServletResponse) (filterResponse)).setStatus(SC_OK);
                String buf = FileCopyUtils.copyToString(filterRequest.getReader());
                Assert.assertEquals(requestBody, buf);
            }
        };
        filter.doFilter(request, response, filterChain);
        Assert.assertNotNull(filter.afterRequestMessage);
        Assert.assertTrue(filter.afterRequestMessage.contains(requestBody));
    }

    @Test
    public void payloadMaxLength() throws Exception {
        setIncludePayload(true);
        setMaxPayloadLength(3);
        final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/hotels");
        MockHttpServletResponse response = new MockHttpServletResponse();
        final byte[] requestBody = "Hello World".getBytes("UTF-8");
        request.setContent(requestBody);
        FilterChain filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest filterRequest, ServletResponse filterResponse) throws IOException, ServletException {
                ((HttpServletResponse) (filterResponse)).setStatus(SC_OK);
                byte[] buf = FileCopyUtils.copyToByteArray(filterRequest.getInputStream());
                Assert.assertArrayEquals(requestBody, buf);
                ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(filterRequest, ContentCachingRequestWrapper.class);
                Assert.assertArrayEquals("Hel".getBytes("UTF-8"), wrapper.getContentAsByteArray());
            }
        };
        filter.doFilter(request, response, filterChain);
        Assert.assertNotNull(filter.afterRequestMessage);
        Assert.assertTrue(filter.afterRequestMessage.contains("Hel"));
        Assert.assertFalse(filter.afterRequestMessage.contains("Hello World"));
    }

    private static class MyRequestLoggingFilter extends AbstractRequestLoggingFilter {
        private String beforeRequestMessage;

        private String afterRequestMessage;

        @Override
        protected void beforeRequest(HttpServletRequest request, String message) {
            this.beforeRequestMessage = message;
        }

        @Override
        protected void afterRequest(HttpServletRequest request, String message) {
            this.afterRequestMessage = message;
        }
    }

    private static class NoOpFilterChain implements FilterChain {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        }
    }
}

