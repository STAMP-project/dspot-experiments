/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.MultiMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpURITest {
    @Test
    public void testInvalidAddress() throws Exception {
        assertInvalidURI("http://[ffff::1:8080/", "Invalid URL; no closing ']' -- should throw exception");
        assertInvalidURI("**", "only '*', not '**'");
        assertInvalidURI("*/", "only '*', not '*/'");
    }

    @Test
    public void testParse() {
        HttpURI uri = new HttpURI();
        uri.parse("*");
        MatcherAssert.assertThat(uri.getHost(), Matchers.nullValue());
        MatcherAssert.assertThat(uri.getPath(), Matchers.is("*"));
        uri.parse("/foo/bar");
        MatcherAssert.assertThat(uri.getHost(), Matchers.nullValue());
        MatcherAssert.assertThat(uri.getPath(), Matchers.is("/foo/bar"));
        uri.parse("//foo/bar");
        MatcherAssert.assertThat(uri.getHost(), Matchers.is("foo"));
        MatcherAssert.assertThat(uri.getPath(), Matchers.is("/bar"));
        uri.parse("http://foo/bar");
        MatcherAssert.assertThat(uri.getHost(), Matchers.is("foo"));
        MatcherAssert.assertThat(uri.getPath(), Matchers.is("/bar"));
    }

    @Test
    public void testParseRequestTarget() {
        HttpURI uri = new HttpURI();
        uri.parseRequestTarget("GET", "*");
        MatcherAssert.assertThat(uri.getHost(), Matchers.nullValue());
        MatcherAssert.assertThat(uri.getPath(), Matchers.is("*"));
        uri.parseRequestTarget("GET", "/foo/bar");
        MatcherAssert.assertThat(uri.getHost(), Matchers.nullValue());
        MatcherAssert.assertThat(uri.getPath(), Matchers.is("/foo/bar"));
        uri.parseRequestTarget("GET", "//foo/bar");
        MatcherAssert.assertThat(uri.getHost(), Matchers.nullValue());
        MatcherAssert.assertThat(uri.getPath(), Matchers.is("//foo/bar"));
        uri.parseRequestTarget("GET", "http://foo/bar");
        MatcherAssert.assertThat(uri.getHost(), Matchers.is("foo"));
        MatcherAssert.assertThat(uri.getPath(), Matchers.is("/bar"));
    }

    @Test
    public void testExtB() throws Exception {
        for (String value : new String[]{ "a", "abcdABCD", "\u00c0", "\u697c", "\ud869\uded5", "\ud840\udc08" }) {
            HttpURI uri = new HttpURI(("/path?value=" + (URLEncoder.encode(value, "UTF-8"))));
            MultiMap<String> parameters = new MultiMap();
            uri.decodeQueryTo(parameters, StandardCharsets.UTF_8);
            Assertions.assertEquals(value, parameters.getString("value"));
        }
    }

    @Test
    public void testAt() throws Exception {
        HttpURI uri = new HttpURI("/@foo/bar");
        Assertions.assertEquals("/@foo/bar", uri.getPath());
    }

    @Test
    public void testParams() throws Exception {
        HttpURI uri = new HttpURI("/foo/bar");
        Assertions.assertEquals("/foo/bar", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        Assertions.assertEquals(null, uri.getParam());
        uri = new HttpURI("/foo/bar;jsessionid=12345");
        Assertions.assertEquals("/foo/bar;jsessionid=12345", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        Assertions.assertEquals("jsessionid=12345", uri.getParam());
        uri = new HttpURI("/foo;abc=123/bar;jsessionid=12345");
        Assertions.assertEquals("/foo;abc=123/bar;jsessionid=12345", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        Assertions.assertEquals("jsessionid=12345", uri.getParam());
        uri = new HttpURI("/foo;abc=123/bar;jsessionid=12345?name=value");
        Assertions.assertEquals("/foo;abc=123/bar;jsessionid=12345", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        Assertions.assertEquals("jsessionid=12345", uri.getParam());
        uri = new HttpURI("/foo;abc=123/bar;jsessionid=12345#target");
        Assertions.assertEquals("/foo;abc=123/bar;jsessionid=12345", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        Assertions.assertEquals("jsessionid=12345", uri.getParam());
    }

    @Test
    public void testMutableURI() {
        HttpURI uri = new HttpURI("/foo/bar");
        Assertions.assertEquals("/foo/bar", uri.toString());
        Assertions.assertEquals("/foo/bar", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        uri.setScheme("http");
        Assertions.assertEquals("http:/foo/bar", uri.toString());
        Assertions.assertEquals("/foo/bar", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        uri.setAuthority("host", 0);
        Assertions.assertEquals("http://host/foo/bar", uri.toString());
        Assertions.assertEquals("/foo/bar", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        uri.setAuthority("host", 8888);
        Assertions.assertEquals("http://host:8888/foo/bar", uri.toString());
        Assertions.assertEquals("/foo/bar", uri.getPath());
        Assertions.assertEquals("/foo/bar", uri.getDecodedPath());
        uri.setPathQuery("/f%30%30;p0/bar;p1;p2");
        Assertions.assertEquals("http://host:8888/f%30%30;p0/bar;p1;p2", uri.toString());
        Assertions.assertEquals("/f%30%30;p0/bar;p1;p2", uri.getPath());
        Assertions.assertEquals("/f00/bar", uri.getDecodedPath());
        Assertions.assertEquals("p2", uri.getParam());
        Assertions.assertEquals(null, uri.getQuery());
        uri.setPathQuery("/f%30%30;p0/bar;p1;p2?name=value");
        Assertions.assertEquals("http://host:8888/f%30%30;p0/bar;p1;p2?name=value", uri.toString());
        Assertions.assertEquals("/f%30%30;p0/bar;p1;p2", uri.getPath());
        Assertions.assertEquals("/f00/bar", uri.getDecodedPath());
        Assertions.assertEquals("p2", uri.getParam());
        Assertions.assertEquals("name=value", uri.getQuery());
        uri.setQuery("other=123456");
        Assertions.assertEquals("http://host:8888/f%30%30;p0/bar;p1;p2?other=123456", uri.toString());
        Assertions.assertEquals("/f%30%30;p0/bar;p1;p2", uri.getPath());
        Assertions.assertEquals("/f00/bar", uri.getDecodedPath());
        Assertions.assertEquals("p2", uri.getParam());
        Assertions.assertEquals("other=123456", uri.getQuery());
    }

    @Test
    public void testSchemeAndOrAuthority() throws Exception {
        HttpURI uri = new HttpURI("/path/info");
        Assertions.assertEquals("/path/info", uri.toString());
        uri.setAuthority("host", 0);
        Assertions.assertEquals("//host/path/info", uri.toString());
        uri.setAuthority("host", 8888);
        Assertions.assertEquals("//host:8888/path/info", uri.toString());
        uri.setScheme("http");
        Assertions.assertEquals("http://host:8888/path/info", uri.toString());
        uri.setAuthority(null, 0);
        Assertions.assertEquals("http:/path/info", uri.toString());
    }

    @Test
    public void testBasicAuthCredentials() throws Exception {
        HttpURI uri = new HttpURI("http://user:password@example.com:8888/blah");
        Assertions.assertEquals("http://user:password@example.com:8888/blah", uri.toString());
        Assertions.assertEquals(uri.getAuthority(), "example.com:8888");
        Assertions.assertEquals(uri.getUser(), "user:password");
    }
}

