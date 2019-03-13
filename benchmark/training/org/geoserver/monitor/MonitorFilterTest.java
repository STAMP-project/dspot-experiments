/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor;


import MonitorFilter.GEOWEBCACHE_CACHE_RESULT;
import MonitorFilter.GEOWEBCACHE_MISS_REASON;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class MonitorFilterTest {
    DummyMonitorDAO dao;

    MonitorFilter filter;

    MockFilterChain chain;

    static final int MAX_BODY_SIZE = 10;

    static final int LONG_BODY_SIZE = 3 * (MonitorFilterTest.MAX_BODY_SIZE);

    @Test
    public void testSimple() throws Exception {
        HttpServletRequest req = request("GET", "/foo/bar", "12.34.56.78", null, null);
        filter.doFilter(req, response(), chain);
        RequestData data = dao.getLast();
        Assert.assertEquals("GET", data.getHttpMethod());
        Assert.assertEquals("/foo/bar", data.getPath());
        Assert.assertEquals("12.34.56.78", data.getRemoteAddr());
        Assert.assertNull(data.getHttpReferer());
    }

    @Test
    public void testWithBody() throws Exception {
        chain = new MockFilterChain(new HttpServlet() {
            @Override
            public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                req.getInputStream().read(new byte[MonitorFilterTest.LONG_BODY_SIZE]);
                res.getOutputStream().write("hello".getBytes());
            }
        });
        HttpServletRequest req = request("POST", "/bar/foo", "78.56.34.12", "baz", null);
        filter.doFilter(req, response(), chain);
        RequestData data = dao.getLast();
        Assert.assertEquals("POST", data.getHttpMethod());
        Assert.assertEquals("/bar/foo", data.getPath());
        Assert.assertEquals("78.56.34.12", data.getRemoteAddr());
        Assert.assertNull(data.getHttpReferer());
        Assert.assertEquals(new String(data.getBody()), "baz");
        Assert.assertEquals(3, data.getBodyContentLength());
        Assert.assertEquals(5, data.getResponseLength());
    }

    @Test
    public void testWithLongBody() throws Exception {
        chain = new MockFilterChain(new HttpServlet() {
            @Override
            public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                req.getInputStream().read(new byte[MonitorFilterTest.LONG_BODY_SIZE]);
                res.getOutputStream().write("hello".getBytes());
            }
        });
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < (MonitorFilterTest.MAX_BODY_SIZE); i++) {
            b.append('b');
        }
        String wanted_body = b.toString();
        for (int i = MonitorFilterTest.MAX_BODY_SIZE; i < (MonitorFilterTest.LONG_BODY_SIZE); i++) {
            b.append('b');
        }
        String given_body = b.toString();
        HttpServletRequest req = request("POST", "/bar/foo", "78.56.34.12", given_body, null);
        filter.doFilter(req, response(), chain);
        RequestData data = dao.getLast();
        Assert.assertEquals(wanted_body, new String(data.getBody()));// Should be trimmed to the maximum length

        Assert.assertEquals(MonitorFilterTest.LONG_BODY_SIZE, data.getBodyContentLength());// Should be the full length, not the trimmed one

    }

    @Test
    public void testWithUnboundedBody() throws Exception {
        final int UNBOUNDED_BODY_SIZE = 10000;// Something really big

        filter.monitor.config.props.put("maxBodySize", Integer.toString(UNBOUNDED_BODY_SIZE));// Ensure the configured property is correct for the

        // tests
        chain = new MockFilterChain(new HttpServlet() {
            @Override
            public void service(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                while ((req.getInputStream().read()) != (-1));// "read" the stream until the end.

                req.getInputStream().read();
                res.getOutputStream().write("hello".getBytes());
            }
        });
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < UNBOUNDED_BODY_SIZE; i++) {
            b.append((i % 10));
        }
        String wanted_body = b.toString();
        String given_body = b.toString();
        HttpServletRequest req = request("POST", "/bar/foo", "78.56.34.12", given_body, null);
        filter.doFilter(req, response(), chain);
        RequestData data = dao.getLast();
        Assert.assertEquals(wanted_body, new String(data.getBody()));// Should be trimmed to the maximum length

        Assert.assertEquals(UNBOUNDED_BODY_SIZE, data.getBodyContentLength());// Should be the full length, not the trimmed one

    }

    @Test
    public void testReferer() throws Exception {
        HttpServletRequest req = request("GET", "/foo/bar", "12.34.56.78", null, "http://testhost/testpath");
        filter.doFilter(req, response(), chain);
        RequestData data = dao.getLast();
        Assert.assertEquals("GET", data.getHttpMethod());
        Assert.assertEquals("/foo/bar", data.getPath());
        Assert.assertEquals("12.34.56.78", data.getRemoteAddr());
        Assert.assertEquals("http://testhost/testpath", data.getHttpReferer());
    }

    @Test
    public void testReferrer() throws Exception {
        // "Referrer" was misspelled in the HTTP spec, check if it works with the "correct"
        // spelling.
        MockHttpServletRequest req = request("POST", "/bar/foo", "78.56.34.12", null, null);
        addHeader("Referrer", "http://testhost/testpath");
        filter.doFilter(req, response(), chain);
        RequestData data = dao.getLast();
        Assert.assertEquals("POST", data.getHttpMethod());
        Assert.assertEquals("/bar/foo", data.getPath());
        Assert.assertEquals("78.56.34.12", data.getRemoteAddr());
        Assert.assertEquals("http://testhost/testpath", data.getHttpReferer());
        Assert.assertNull(data.getCacheResult());
        Assert.assertNull(data.getMissReason());
    }

    @Test
    public void testGWCHeaders() throws Exception {
        MockHttpServletRequest req = request("POST", "/bar/foo", "78.56.34.12", null, null);
        MockHttpServletResponse response = response();
        String cacheResult = "Miss";
        response.addHeader(GEOWEBCACHE_CACHE_RESULT, cacheResult);
        String missReason = "Wrong planet alignment";
        response.addHeader(GEOWEBCACHE_MISS_REASON, missReason);
        filter.doFilter(req, response, chain);
        RequestData data = dao.getLast();
        Assert.assertEquals("POST", data.getHttpMethod());
        Assert.assertEquals("/bar/foo", data.getPath());
        Assert.assertEquals("78.56.34.12", data.getRemoteAddr());
        Assert.assertEquals(cacheResult, data.getCacheResult());
        Assert.assertEquals(missReason, data.getMissReason());
    }

    @Test
    public void testUserRemoteUser() throws Exception {
        Object principal = new org.springframework.security.core.userdetails.User("username", "", Collections.<GrantedAuthority>emptyList());
        testRemoteUser(principal);
    }

    @Test
    public void testUserDetailsRemoteUser() throws Exception {
        UserDetails principal = createMock(UserDetails.class);
        expect(principal.getUsername()).andReturn("username");
        replay(principal);
        testRemoteUser(principal);
    }
}

