/**
 * Copyright 2002-2018 the original author or authors.
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


import DispatcherType.FORWARD;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockFilterChain;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * Unit tests for {@link ForwardedHeaderFilter}.
 *
 * @author Rossen Stoyanchev
 * @author Edd? Mel?ndez
 * @author Rob Winch
 */
public class ForwardedHeaderFilterTests {
    private static final String X_FORWARDED_PROTO = "x-forwarded-proto";// SPR-14372 (case insensitive)


    private static final String X_FORWARDED_HOST = "x-forwarded-host";

    private static final String X_FORWARDED_PORT = "x-forwarded-port";

    private static final String X_FORWARDED_PREFIX = "x-forwarded-prefix";

    private static final String X_FORWARDED_SSL = "x-forwarded-ssl";

    private final ForwardedHeaderFilter filter = new ForwardedHeaderFilter();

    private MockHttpServletRequest request;

    private MockFilterChain filterChain;

    @Test
    public void contextPathEmpty() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "");
        Assert.assertEquals("", filterAndGetContextPath());
    }

    @Test
    public void contextPathWithTrailingSlash() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/foo/bar/");
        Assert.assertEquals("/foo/bar", filterAndGetContextPath());
    }

    @Test
    public void contextPathWithTrailingSlashes() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/foo/bar/baz///");
        Assert.assertEquals("/foo/bar/baz", filterAndGetContextPath());
    }

    @Test
    public void contextPathWithForwardedPrefix() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/prefix");
        this.request.setContextPath("/mvc-showcase");
        String actual = filterAndGetContextPath();
        Assert.assertEquals("/prefix", actual);
    }

    @Test
    public void contextPathWithForwardedPrefixTrailingSlash() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/prefix/");
        this.request.setContextPath("/mvc-showcase");
        String actual = filterAndGetContextPath();
        Assert.assertEquals("/prefix", actual);
    }

    @Test
    public void contextPathPreserveEncoding() throws Exception {
        this.request.setContextPath("/app%20");
        this.request.setRequestURI("/app%20/path/");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("/app%20", actual.getContextPath());
        Assert.assertEquals("/app%20/path/", actual.getRequestURI());
        Assert.assertEquals("http://localhost/app%20/path/", actual.getRequestURL().toString());
    }

    @Test
    public void requestUri() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/");
        this.request.setContextPath("/app");
        this.request.setRequestURI("/app/path");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("", actual.getContextPath());
        Assert.assertEquals("/path", actual.getRequestURI());
    }

    @Test
    public void requestUriWithTrailingSlash() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/");
        this.request.setContextPath("/app");
        this.request.setRequestURI("/app/path/");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("", actual.getContextPath());
        Assert.assertEquals("/path/", actual.getRequestURI());
    }

    @Test
    public void requestUriPreserveEncoding() throws Exception {
        this.request.setContextPath("/app");
        this.request.setRequestURI("/app/path%20with%20spaces/");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("/app", actual.getContextPath());
        Assert.assertEquals("/app/path%20with%20spaces/", actual.getRequestURI());
        Assert.assertEquals("http://localhost/app/path%20with%20spaces/", actual.getRequestURL().toString());
    }

    @Test
    public void requestUriEqualsContextPath() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/");
        this.request.setContextPath("/app");
        this.request.setRequestURI("/app");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("", actual.getContextPath());
        Assert.assertEquals("/", actual.getRequestURI());
    }

    @Test
    public void requestUriRootUrl() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/");
        this.request.setContextPath("/app");
        this.request.setRequestURI("/app/");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("", actual.getContextPath());
        Assert.assertEquals("/", actual.getRequestURI());
    }

    @Test
    public void requestUriPreserveSemicolonContent() throws Exception {
        this.request.setContextPath("");
        this.request.setRequestURI("/path;a=b/with/semicolon");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("", actual.getContextPath());
        Assert.assertEquals("/path;a=b/with/semicolon", actual.getRequestURI());
        Assert.assertEquals("http://localhost/path;a=b/with/semicolon", actual.getRequestURL().toString());
    }

    @Test
    public void caseInsensitiveForwardedPrefix() throws Exception {
        this.request = new MockHttpServletRequest() {
            // SPR-14372: make it case-sensitive
            @Override
            public String getHeader(String header) {
                Enumeration<String> names = getHeaderNames();
                while (names.hasMoreElements()) {
                    String name = names.nextElement();
                    if (name.equals(header)) {
                        return super.getHeader(header);
                    }
                } 
                return null;
            }
        };
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/prefix");
        this.request.setRequestURI("/path");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("/prefix/path", actual.getRequestURI());
    }

    @Test
    public void shouldFilter() {
        testShouldFilter("Forwarded");
        testShouldFilter(ForwardedHeaderFilterTests.X_FORWARDED_HOST);
        testShouldFilter(ForwardedHeaderFilterTests.X_FORWARDED_PORT);
        testShouldFilter(ForwardedHeaderFilterTests.X_FORWARDED_PROTO);
        testShouldFilter(ForwardedHeaderFilterTests.X_FORWARDED_SSL);
    }

    @Test
    public void shouldNotFilter() {
        Assert.assertTrue(this.filter.shouldNotFilter(new MockHttpServletRequest()));
    }

    @Test
    public void forwardedRequest() throws Exception {
        this.request.setRequestURI("/mvc-showcase");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "84.198.58.199");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.request.addHeader("foo", "bar");
        this.filter.doFilter(this.request, new MockHttpServletResponse(), this.filterChain);
        HttpServletRequest actual = ((HttpServletRequest) (this.filterChain.getRequest()));
        Assert.assertEquals("https://84.198.58.199/mvc-showcase", actual.getRequestURL().toString());
        Assert.assertEquals("https", actual.getScheme());
        Assert.assertEquals("84.198.58.199", actual.getServerName());
        Assert.assertEquals(443, actual.getServerPort());
        Assert.assertTrue(actual.isSecure());
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO));
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST));
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT));
        Assert.assertEquals("bar", actual.getHeader("foo"));
    }

    @Test
    public void forwardedRequestInRemoveOnlyMode() throws Exception {
        this.request.setRequestURI("/mvc-showcase");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "84.198.58.199");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_SSL, "on");
        this.request.addHeader("foo", "bar");
        this.filter.setRemoveOnly(true);
        this.filter.doFilter(this.request, new MockHttpServletResponse(), this.filterChain);
        HttpServletRequest actual = ((HttpServletRequest) (this.filterChain.getRequest()));
        Assert.assertEquals("http://localhost/mvc-showcase", actual.getRequestURL().toString());
        Assert.assertEquals("http", actual.getScheme());
        Assert.assertEquals("localhost", actual.getServerName());
        Assert.assertEquals(80, actual.getServerPort());
        Assert.assertFalse(actual.isSecure());
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO));
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST));
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT));
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_SSL));
        Assert.assertEquals("bar", actual.getHeader("foo"));
    }

    @Test
    public void forwardedRequestWithSsl() throws Exception {
        this.request.setRequestURI("/mvc-showcase");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_SSL, "on");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "84.198.58.199");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.request.addHeader("foo", "bar");
        this.filter.doFilter(this.request, new MockHttpServletResponse(), this.filterChain);
        HttpServletRequest actual = ((HttpServletRequest) (this.filterChain.getRequest()));
        Assert.assertEquals("https://84.198.58.199/mvc-showcase", actual.getRequestURL().toString());
        Assert.assertEquals("https", actual.getScheme());
        Assert.assertEquals("84.198.58.199", actual.getServerName());
        Assert.assertEquals(443, actual.getServerPort());
        Assert.assertTrue(actual.isSecure());
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_SSL));
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST));
        Assert.assertNull(actual.getHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT));
        Assert.assertEquals("bar", actual.getHeader("foo"));
    }

    // SPR-16983
    @Test
    public void forwardedRequestWithServletForward() throws Exception {
        this.request.setRequestURI("/foo");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "www.mycompany.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.filter.doFilter(this.request, new MockHttpServletResponse(), this.filterChain);
        HttpServletRequest wrappedRequest = ((HttpServletRequest) (this.filterChain.getRequest()));
        this.request.setDispatcherType(FORWARD);
        this.request.setRequestURI("/bar");
        this.filterChain.reset();
        this.filter.doFilter(wrappedRequest, new MockHttpServletResponse(), this.filterChain);
        HttpServletRequest actual = ((HttpServletRequest) (this.filterChain.getRequest()));
        Assert.assertNotNull(actual);
        Assert.assertEquals("/bar", actual.getRequestURI());
        Assert.assertEquals("https://www.mycompany.com/bar", actual.getRequestURL().toString());
    }

    @Test
    public void requestUriWithForwardedPrefix() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/prefix");
        this.request.setRequestURI("/mvc-showcase");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("http://localhost/prefix/mvc-showcase", actual.getRequestURL().toString());
    }

    @Test
    public void requestUriWithForwardedPrefixTrailingSlash() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/prefix/");
        this.request.setRequestURI("/mvc-showcase");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        Assert.assertEquals("http://localhost/prefix/mvc-showcase", actual.getRequestURL().toString());
    }

    @Test
    public void requestURLNewStringBuffer() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PREFIX, "/prefix/");
        this.request.setRequestURI("/mvc-showcase");
        HttpServletRequest actual = filterAndGetWrappedRequest();
        actual.getRequestURL().append("?key=value");
        Assert.assertEquals("http://localhost/prefix/mvc-showcase", actual.getRequestURL().toString());
    }

    @Test
    public void sendRedirectWithAbsolutePath() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        String redirectedUrl = sendRedirect("/foo/bar");
        Assert.assertEquals("https://example.com/foo/bar", redirectedUrl);
    }

    // SPR-16506
    @Test
    public void sendRedirectWithAbsolutePathQueryParamAndFragment() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.request.setQueryString("oldqp=1");
        String redirectedUrl = sendRedirect("/foo/bar?newqp=2#fragment");
        Assert.assertEquals("https://example.com/foo/bar?newqp=2#fragment", redirectedUrl);
    }

    @Test
    public void sendRedirectWithContextPath() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.request.setContextPath("/context");
        String redirectedUrl = sendRedirect("/context/foo/bar");
        Assert.assertEquals("https://example.com/context/foo/bar", redirectedUrl);
    }

    @Test
    public void sendRedirectWithRelativePath() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.request.setRequestURI("/parent/");
        String redirectedUrl = sendRedirect("foo/bar");
        Assert.assertEquals("https://example.com/parent/foo/bar", redirectedUrl);
    }

    @Test
    public void sendRedirectWithFileInPathAndRelativeRedirect() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.request.setRequestURI("/context/a");
        String redirectedUrl = sendRedirect("foo/bar");
        Assert.assertEquals("https://example.com/context/foo/bar", redirectedUrl);
    }

    @Test
    public void sendRedirectWithRelativePathIgnoresFile() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.request.setRequestURI("/parent");
        String redirectedUrl = sendRedirect("foo/bar");
        Assert.assertEquals("https://example.com/foo/bar", redirectedUrl);
    }

    @Test
    public void sendRedirectWithLocationDotDotPath() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        String redirectedUrl = sendRedirect("parent/../foo/bar");
        Assert.assertEquals("https://example.com/foo/bar", redirectedUrl);
    }

    @Test
    public void sendRedirectWithLocationHasScheme() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        String location = "http://other.info/foo/bar";
        String redirectedUrl = sendRedirect(location);
        Assert.assertEquals(location, redirectedUrl);
    }

    @Test
    public void sendRedirectWithLocationSlashSlash() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        String location = "//other.info/foo/bar";
        String redirectedUrl = sendRedirect(location);
        Assert.assertEquals(("https:" + location), redirectedUrl);
    }

    @Test
    public void sendRedirectWithLocationSlashSlashParentDotDot() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        String location = "//other.info/parent/../foo/bar";
        String redirectedUrl = sendRedirect(location);
        Assert.assertEquals(("https:" + location), redirectedUrl);
    }

    @Test
    public void sendRedirectWithNoXForwardedAndAbsolutePath() throws Exception {
        String redirectedUrl = sendRedirect("/foo/bar");
        Assert.assertEquals("/foo/bar", redirectedUrl);
    }

    @Test
    public void sendRedirectWithNoXForwardedAndDotDotPath() throws Exception {
        String redirectedUrl = sendRedirect("../foo/bar");
        Assert.assertEquals("../foo/bar", redirectedUrl);
    }

    @Test
    public void sendRedirectWhenRequestOnlyAndXForwardedThenUsesRelativeRedirects() throws Exception {
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PROTO, "https");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_HOST, "example.com");
        this.request.addHeader(ForwardedHeaderFilterTests.X_FORWARDED_PORT, "443");
        this.filter.setRelativeRedirects(true);
        String location = sendRedirect("/a");
        Assert.assertEquals("/a", location);
    }

    @Test
    public void sendRedirectWhenRequestOnlyAndNoXForwardedThenUsesRelativeRedirects() throws Exception {
        this.filter.setRelativeRedirects(true);
        String location = sendRedirect("/a");
        Assert.assertEquals("/a", location);
    }
}

