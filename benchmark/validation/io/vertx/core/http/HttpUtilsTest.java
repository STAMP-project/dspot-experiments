/**
 * Copyright (c) 2011-2018 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.http;


import io.vertx.core.http.impl.HttpUtils;
import org.junit.Assert;
import org.junit.Test;


public class HttpUtilsTest {
    @Test
    public void testParseKeepAliveTimeout() {
        HttpUtilsTest.assertKeepAliveTimeout("timeout=5", 5);
        HttpUtilsTest.assertKeepAliveTimeout(" timeout=5", 5);
        HttpUtilsTest.assertKeepAliveTimeout("timeout=5 ", 5);
        HttpUtilsTest.assertKeepAliveTimeout("a=4,timeout=5", 5);
        HttpUtilsTest.assertKeepAliveTimeout(" a=4,timeout=5", 5);
        HttpUtilsTest.assertKeepAliveTimeout("a=4 ,timeout=5", 5);
        HttpUtilsTest.assertKeepAliveTimeout("a=4, timeout=5", 5);
        HttpUtilsTest.assertKeepAliveTimeout("a=4,timeout=5 ", 5);
        HttpUtilsTest.assertKeepAliveTimeout("", (-1));
        HttpUtilsTest.assertKeepAliveTimeout("a=4", (-1));
        HttpUtilsTest.assertKeepAliveTimeout("timeout", (-1));
        HttpUtilsTest.assertKeepAliveTimeout("timeout=", (-1));
        HttpUtilsTest.assertKeepAliveTimeout("timeout=a", (-1));
        HttpUtilsTest.assertKeepAliveTimeout("timeout=-5", (-1));
        HttpUtilsTest.assertKeepAliveTimeout("timeout=5_", (-1));
    }

    @Test
    public void testResolveUri() throws Exception {
        assertResolveUri("http://a/b/c/g", "http://a/b/c/d;p?q", "g");
        assertResolveUri("http://a/b/c/g", "http://a/b/c/d;p?q", "./g");
        assertResolveUri("http://a/b/c/g/", "http://a/b/c/d;p?q", "g/");
        assertResolveUri("http://a/g", "http://a/b/c/d;p?q", "/g");
        assertResolveUri("http://g", "http://a/b/c/d;p?q", "//g");
        assertResolveUri("http://a/b/c/d;p?y", "http://a/b/c/d;p?q", "?y");
        assertResolveUri("http://a/b/c/g?y", "http://a/b/c/d;p?q", "g?y");
        assertResolveUri("http://a/b/c/d;p?q#s", "http://a/b/c/d;p?q", "#s");
        assertResolveUri("http://a/b/c/g#s", "http://a/b/c/d;p?q", "g#s");
        assertResolveUri("http://a/b/c/;x", "http://a/b/c/d;p?q", ";x");
        assertResolveUri("http://a/b/c/g;x", "http://a/b/c/d;p?q", "g;x");
        assertResolveUri("http://a/b/c/g;x?y#s", "http://a/b/c/d;p?q", "g;x?y#s");
        assertResolveUri("http://a/b/c/d;p?q", "http://a/b/c/d;p?q", "");
        assertResolveUri("http://a/b/c/", "http://a/b/c/d;p?q", ".");
        assertResolveUri("http://a/b/c/", "http://a/b/c/d;p?q", "./");
        assertResolveUri("http://a/b/", "http://a/b/c/d;p?q", "..");
        assertResolveUri("http://a/", "http://a/b/c/d;p?q", "../..");
        assertResolveUri("http://a/", "http://a/b/c/d;p?q", "../../");
        assertResolveUri("http://a/g", "http://a/b/c/d;p?q", "../../g");
        assertResolveUri("http://a/g", "http://a/b/c/d;p?q", "../../../g");
        assertResolveUri("http://a/g", "http://a/b/c/d;p?q", "../../../../g");
        assertResolveUri("http://example.com/path", "https://example.com/path", "http://example.com/path");
        assertResolveUri("https://example.com/relativeUrl", "https://example.com/path?q=2", "/relativeUrl");
        assertResolveUri("https://example.com/path?q=2#test", "https://example.com/path?q=2", "#test");// correct ?

        assertResolveUri("https://example.com/relativePath?q=3", "https://example.com/path?q=2", "/relativePath?q=3");
        assertResolveUri("https://example.com/path?q=3", "https://example.com/path?q=2", "?q=3");// correct ?

    }

    @Test
    public void testNoLeadingSlash() throws Exception {
        Assert.assertEquals("/path/with/no/leading/slash", HttpUtils.normalizePath("path/with/no/leading/slash"));
    }

    @Test
    public void testNullPath() throws Exception {
        Assert.assertNull(HttpUtils.normalizePath(null));
    }

    @Test
    public void testPathWithSpaces1() throws Exception {
        // this is a special case since only percent encoded values should be unescaped from the path
        Assert.assertEquals("/foo+blah/eek", HttpUtils.normalizePath("/foo+blah/eek"));
    }

    @Test
    public void testPathWithSpaces2() throws Exception {
        Assert.assertEquals("/foo%20blah/eek", HttpUtils.normalizePath("/foo%20blah/eek"));
    }

    @Test
    public void testDodgyPath1() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("/foo/../../blah"));
    }

    @Test
    public void testDodgyPath2() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("/foo/../../../blah"));
    }

    @Test
    public void testDodgyPath3() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("/foo/../blah"));
    }

    @Test
    public void testDodgyPath4() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("/../blah"));
    }

    @Test
    public void testMultipleSlashPath1() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("//blah"));
    }

    @Test
    public void testMultipleSlashPath2() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("///blah"));
    }

    @Test
    public void testMultipleSlashPath3() throws Exception {
        Assert.assertEquals("/foo/blah", HttpUtils.normalizePath("/foo//blah"));
    }

    @Test
    public void testMultipleSlashPath4() throws Exception {
        Assert.assertEquals("/foo/blah/", HttpUtils.normalizePath("/foo//blah///"));
    }

    @Test
    public void testSlashesAndDodgyPath1() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("//../blah"));
    }

    @Test
    public void testSlashesAndDodgyPath2() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("/..//blah"));
    }

    @Test
    public void testSlashesAndDodgyPath3() throws Exception {
        Assert.assertEquals("/blah", HttpUtils.normalizePath("//..//blah"));
    }

    @Test
    public void testDodgyPathEncoded() throws Exception {
        Assert.assertEquals("/..%2Fblah", HttpUtils.normalizePath("/%2E%2E%2Fblah"));
    }

    @Test
    public void testTrailingSlash() throws Exception {
        Assert.assertEquals("/blah/", HttpUtils.normalizePath("/blah/"));
    }

    @Test
    public void testMultipleTrailingSlashes1() throws Exception {
        Assert.assertEquals("/blah/", HttpUtils.normalizePath("/blah//"));
    }

    @Test
    public void testMultipleTrailingSlashes2() throws Exception {
        Assert.assertEquals("/blah/", HttpUtils.normalizePath("/blah///"));
    }

    @Test
    public void testBadURL() throws Exception {
        try {
            HttpUtils.normalizePath("/%7B%channel%%7D");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected!
        }
    }

    @Test
    public void testDoubleDot() throws Exception {
        Assert.assertEquals("/foo/bar/abc..def", HttpUtils.normalizePath("/foo/bar/abc..def"));
    }

    @Test
    public void testSpec() throws Exception {
        Assert.assertEquals("/a/g", HttpUtils.normalizePath("/a/b/c/./../../g"));
        Assert.assertEquals("/mid/6", HttpUtils.normalizePath("mid/content=5/../6"));
        Assert.assertEquals("/~username/", HttpUtils.normalizePath("/%7Eusername/"));
        Assert.assertEquals("/b/", HttpUtils.normalizePath("/b/c/.."));
    }
}

