/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security;


import GeoServerSecurityFilterChain.ANONYMOUS_FILTER;
import GeoServerSecurityFilterChain.BASIC_AUTH_FILTER;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.geoserver.security.config.SecurityFilterConfig;
import org.geoserver.security.config.SecurityNamedServiceConfig;
import org.geoserver.security.filter.GeoServerAuthenticationFilter;
import org.geoserver.security.filter.GeoServerSecurityFilter;
import org.geoserver.security.validation.SecurityConfigException;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.SystemTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletResponse;


@Category(SystemTest.class)
public class GeoServerCustomFilterTest extends GeoServerSystemTestSupport {
    enum Pos {

        FIRST,
        LAST,
        BEFORE,
        AFTER;}

    @Test
    public void testInactive() throws Exception {
        HttpServletRequest request = createRequest("/foo");
        setMethod("GET");
        MockHttpServletResponse response = dispatch(request);
        Assert.assertNull(response.getHeader("foo"));
    }

    @Test
    public void testFirst() throws Exception {
        setupFilterEntry(GeoServerCustomFilterTest.Pos.FIRST, null, false);
        HttpServletRequest request = createRequest("/foo");
        setMethod("GET");
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals("bar", response.getHeader("foo"));
    }

    @Test
    public void testLast() throws Exception {
        try {
            setupFilterEntry(GeoServerCustomFilterTest.Pos.LAST, null, true);
            Assert.fail("SecurityConfigException missing, anonymous filter must be the last one");
        } catch (SecurityConfigException ex) {
        }
    }

    @Test
    public void testBefore() throws Exception {
        setupFilterEntry(GeoServerCustomFilterTest.Pos.BEFORE, ANONYMOUS_FILTER, false);
        HttpServletRequest request = createRequest("/foo");
        setMethod("GET");
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals("bar", response.getHeader("foo"));
    }

    @Test
    public void testAfter() throws Exception {
        setupFilterEntry(GeoServerCustomFilterTest.Pos.AFTER, BASIC_AUTH_FILTER, true);
        HttpServletRequest request = createRequest("/foo");
        setMethod("GET");
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals("bar", response.getHeader("foo"));
    }

    static class SecurityProvider extends GeoServerSecurityProvider {
        @Override
        public Class<? extends GeoServerSecurityFilter> getFilterClass() {
            return GeoServerCustomFilterTest.Filter.class;
        }

        @Override
        public GeoServerSecurityFilter createFilter(SecurityNamedServiceConfig config) {
            GeoServerCustomFilterTest.Filter f = new GeoServerCustomFilterTest.Filter();
            f.setAssertAuth(((GeoServerCustomFilterTest.FilterConfig) (config)).isAssertSecurityContext());
            return f;
        }
    }

    static class FilterConfig extends SecurityFilterConfig {
        boolean assertAuth = true;

        public void setAssertAuth(boolean assertAuth) {
            this.assertAuth = assertAuth;
        }

        public boolean isAssertSecurityContext() {
            return assertAuth;
        }
    }

    static class Filter extends GeoServerSecurityFilter implements GeoServerAuthenticationFilter {
        boolean assertAuth = true;

        public Filter() {
        }

        public void setAssertAuth(boolean assertAuth) {
            this.assertAuth = assertAuth;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            // Authentication auth =
            // SecurityContextHolder.getContext().getAuthentication();
            // if (assertAuth) {
            // assertNotNull(auth);
            // }
            // else {
            // assertNull(auth);
            // }
            setHeader("foo", "bar");
            chain.doFilter(request, response);
        }

        @Override
        public boolean applicableForHtml() {
            return true;
        }

        @Override
        public boolean applicableForServices() {
            return true;
        }
    }
}

