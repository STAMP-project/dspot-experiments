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
package org.eclipse.jetty.http.pathmap;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ServletPathSpecTest {
    @Test
    public void testBadServletPathSpecA() {
        assertBadServletPathSpec("foo");
    }

    @Test
    public void testBadServletPathSpecB() {
        assertBadServletPathSpec("/foo/*.do");
    }

    @Test
    public void testBadServletPathSpecC() {
        assertBadServletPathSpec("foo/*.do");
    }

    @Test
    public void testBadServletPathSpecD() {
        assertBadServletPathSpec("foo/*.*do");
    }

    @Test
    public void testBadServletPathSpecE() {
        assertBadServletPathSpec("*do");
    }

    @Test
    public void testDefaultPathSpec() {
        ServletPathSpec spec = new ServletPathSpec("/");
        Assertions.assertEquals("/", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals((-1), spec.getPathDepth(), "Spec.pathDepth");
    }

    @Test
    public void testExactPathSpec() {
        ServletPathSpec spec = new ServletPathSpec("/abs/path");
        Assertions.assertEquals("/abs/path", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals(2, spec.getPathDepth(), "Spec.pathDepth");
        assertMatches(spec, "/abs/path");
        assertNotMatches(spec, "/abs/path/");
        assertNotMatches(spec, "/abs/path/more");
        assertNotMatches(spec, "/foo");
        assertNotMatches(spec, "/foo/abs/path");
        assertNotMatches(spec, "/foo/abs/path/");
    }

    @Test
    public void testGetPathInfo() {
        Assertions.assertEquals(null, new ServletPathSpec("/Foo/bar").getPathInfo("/Foo/bar"), "pathInfo exact");
        Assertions.assertEquals("/bar", new ServletPathSpec("/Foo/*").getPathInfo("/Foo/bar"), "pathInfo prefix");
        Assertions.assertEquals("/*", new ServletPathSpec("/Foo/*").getPathInfo("/Foo/*"), "pathInfo prefix");
        Assertions.assertEquals("/", new ServletPathSpec("/Foo/*").getPathInfo("/Foo/"), "pathInfo prefix");
        Assertions.assertEquals(null, new ServletPathSpec("/Foo/*").getPathInfo("/Foo"), "pathInfo prefix");
        Assertions.assertEquals(null, new ServletPathSpec("*.ext").getPathInfo("/Foo/bar.ext"), "pathInfo suffix");
        Assertions.assertEquals(null, new ServletPathSpec("/").getPathInfo("/Foo/bar.ext"), "pathInfo default");
        Assertions.assertEquals("/xxx/zzz", new ServletPathSpec("/*").getPathInfo("/xxx/zzz"), "pathInfo default");
    }

    @Test
    public void testNullPathSpec() {
        ServletPathSpec spec = new ServletPathSpec(null);
        Assertions.assertEquals("", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals((-1), spec.getPathDepth(), "Spec.pathDepth");
    }

    @Test
    public void testRootPathSpec() {
        ServletPathSpec spec = new ServletPathSpec("");
        Assertions.assertEquals("", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals((-1), spec.getPathDepth(), "Spec.pathDepth");
    }

    @Test
    public void testPathMatch() {
        Assertions.assertEquals("/Foo/bar", new ServletPathSpec("/Foo/bar").getPathMatch("/Foo/bar"), "pathMatch exact");
        Assertions.assertEquals("/Foo", new ServletPathSpec("/Foo/*").getPathMatch("/Foo/bar"), "pathMatch prefix");
        Assertions.assertEquals("/Foo", new ServletPathSpec("/Foo/*").getPathMatch("/Foo/"), "pathMatch prefix");
        Assertions.assertEquals("/Foo", new ServletPathSpec("/Foo/*").getPathMatch("/Foo"), "pathMatch prefix");
        Assertions.assertEquals("/Foo/bar.ext", new ServletPathSpec("*.ext").getPathMatch("/Foo/bar.ext"), "pathMatch suffix");
        Assertions.assertEquals("/Foo/bar.ext", new ServletPathSpec("/").getPathMatch("/Foo/bar.ext"), "pathMatch default");
        Assertions.assertEquals("", new ServletPathSpec("/*").getPathMatch("/xxx/zzz"), "pathMatch default");
    }

    @Test
    public void testPrefixPathSpec() {
        ServletPathSpec spec = new ServletPathSpec("/downloads/*");
        Assertions.assertEquals("/downloads/*", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals(2, spec.getPathDepth(), "Spec.pathDepth");
        assertMatches(spec, "/downloads/logo.jpg");
        assertMatches(spec, "/downloads/distribution.tar.gz");
        assertMatches(spec, "/downloads/distribution.tgz");
        assertMatches(spec, "/downloads/distribution.zip");
        assertMatches(spec, "/downloads");
        Assertions.assertEquals("/", spec.getPathInfo("/downloads/"), "Spec.pathInfo");
        Assertions.assertEquals("/distribution.zip", spec.getPathInfo("/downloads/distribution.zip"), "Spec.pathInfo");
        Assertions.assertEquals("/dist/9.0/distribution.tar.gz", spec.getPathInfo("/downloads/dist/9.0/distribution.tar.gz"), "Spec.pathInfo");
    }

    @Test
    public void testSuffixPathSpec() {
        ServletPathSpec spec = new ServletPathSpec("*.gz");
        Assertions.assertEquals("*.gz", spec.getDeclaration(), "Spec.pathSpec");
        Assertions.assertEquals(0, spec.getPathDepth(), "Spec.pathDepth");
        assertMatches(spec, "/downloads/distribution.tar.gz");
        assertMatches(spec, "/downloads/jetty.log.gz");
        assertNotMatches(spec, "/downloads/distribution.zip");
        assertNotMatches(spec, "/downloads/distribution.tgz");
        assertNotMatches(spec, "/abs/path");
        Assertions.assertEquals(null, spec.getPathInfo("/downloads/distribution.tar.gz"), "Spec.pathInfo");
    }
}

