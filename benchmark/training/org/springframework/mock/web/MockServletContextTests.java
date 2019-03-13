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
package org.springframework.mock.web;


import java.util.Map;
import java.util.Set;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRegistration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @since 19.02.2006
 */
public class MockServletContextTests {
    private final MockServletContext sc = new MockServletContext("org/springframework/mock");

    @Test
    public void listFiles() {
        Set<String> paths = sc.getResourcePaths("/web");
        Assert.assertNotNull(paths);
        Assert.assertTrue(paths.contains("/web/MockServletContextTests.class"));
    }

    @Test
    public void listSubdirectories() {
        Set<String> paths = sc.getResourcePaths("/");
        Assert.assertNotNull(paths);
        Assert.assertTrue(paths.contains("/web/"));
    }

    @Test
    public void listNonDirectory() {
        Set<String> paths = sc.getResourcePaths("/web/MockServletContextTests.class");
        Assert.assertNull(paths);
    }

    @Test
    public void listInvalidPath() {
        Set<String> paths = sc.getResourcePaths("/web/invalid");
        Assert.assertNull(paths);
    }

    @Test
    public void registerContextAndGetContext() {
        MockServletContext sc2 = new MockServletContext();
        sc.setContextPath("/");
        sc.registerContext("/second", sc2);
        Assert.assertSame(sc, sc.getContext("/"));
        Assert.assertSame(sc2, sc.getContext("/second"));
    }

    @Test
    public void getMimeType() {
        Assert.assertEquals("text/html", sc.getMimeType("test.html"));
        Assert.assertEquals("image/gif", sc.getMimeType("test.gif"));
        Assert.assertNull(sc.getMimeType("test.foobar"));
    }

    /**
     * Introduced to dispel claims in a thread on Stack Overflow:
     * <a href="http://stackoverflow.com/questions/22986109/testing-spring-managed-servlet">Testing Spring managed servlet</a>
     */
    @Test
    public void getMimeTypeWithCustomConfiguredType() {
        sc.addMimeType("enigma", new MediaType("text", "enigma"));
        Assert.assertEquals("text/enigma", sc.getMimeType("filename.enigma"));
    }

    @Test
    public void servletVersion() {
        Assert.assertEquals(3, sc.getMajorVersion());
        Assert.assertEquals(1, sc.getMinorVersion());
        Assert.assertEquals(3, sc.getEffectiveMajorVersion());
        Assert.assertEquals(1, sc.getEffectiveMinorVersion());
        sc.setMajorVersion(4);
        sc.setMinorVersion(0);
        sc.setEffectiveMajorVersion(4);
        sc.setEffectiveMinorVersion(0);
        Assert.assertEquals(4, sc.getMajorVersion());
        Assert.assertEquals(0, sc.getMinorVersion());
        Assert.assertEquals(4, sc.getEffectiveMajorVersion());
        Assert.assertEquals(0, sc.getEffectiveMinorVersion());
    }

    @Test
    public void registerAndUnregisterNamedDispatcher() throws Exception {
        final String name = "test-servlet";
        final String url = "/test";
        Assert.assertNull(sc.getNamedDispatcher(name));
        sc.registerNamedDispatcher(name, new MockRequestDispatcher(url));
        RequestDispatcher namedDispatcher = sc.getNamedDispatcher(name);
        Assert.assertNotNull(namedDispatcher);
        MockHttpServletResponse response = new MockHttpServletResponse();
        namedDispatcher.forward(new MockHttpServletRequest(sc), response);
        Assert.assertEquals(url, response.getForwardedUrl());
        sc.unregisterNamedDispatcher(name);
        Assert.assertNull(sc.getNamedDispatcher(name));
    }

    @Test
    public void getNamedDispatcherForDefaultServlet() throws Exception {
        final String name = "default";
        RequestDispatcher namedDispatcher = sc.getNamedDispatcher(name);
        Assert.assertNotNull(namedDispatcher);
        MockHttpServletResponse response = new MockHttpServletResponse();
        namedDispatcher.forward(new MockHttpServletRequest(sc), response);
        Assert.assertEquals(name, response.getForwardedUrl());
    }

    @Test
    public void setDefaultServletName() throws Exception {
        final String originalDefault = "default";
        final String newDefault = "test";
        Assert.assertNotNull(sc.getNamedDispatcher(originalDefault));
        sc.setDefaultServletName(newDefault);
        Assert.assertEquals(newDefault, sc.getDefaultServletName());
        Assert.assertNull(sc.getNamedDispatcher(originalDefault));
        RequestDispatcher namedDispatcher = sc.getNamedDispatcher(newDefault);
        Assert.assertNotNull(namedDispatcher);
        MockHttpServletResponse response = new MockHttpServletResponse();
        namedDispatcher.forward(new MockHttpServletRequest(sc), response);
        Assert.assertEquals(newDefault, response.getForwardedUrl());
    }

    /**
     *
     *
     * @since 4.1.2
     */
    @Test
    public void getServletRegistration() {
        Assert.assertNull(sc.getServletRegistration("servlet"));
    }

    /**
     *
     *
     * @since 4.1.2
     */
    @Test
    public void getServletRegistrations() {
        Map<String, ? extends ServletRegistration> servletRegistrations = sc.getServletRegistrations();
        Assert.assertNotNull(servletRegistrations);
        Assert.assertEquals(0, servletRegistrations.size());
    }

    /**
     *
     *
     * @since 4.1.2
     */
    @Test
    public void getFilterRegistration() {
        Assert.assertNull(sc.getFilterRegistration("filter"));
    }

    /**
     *
     *
     * @since 4.1.2
     */
    @Test
    public void getFilterRegistrations() {
        Map<String, ? extends FilterRegistration> filterRegistrations = sc.getFilterRegistrations();
        Assert.assertNotNull(filterRegistrations);
        Assert.assertEquals(0, filterRegistrations.size());
    }
}

