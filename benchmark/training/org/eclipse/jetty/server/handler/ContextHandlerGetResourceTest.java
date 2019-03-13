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
package org.eclipse.jetty.server.handler;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.toolchain.test.FS;
import org.eclipse.jetty.util.resource.Resource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;


public class ContextHandlerGetResourceTest {
    private static boolean OS_ALIAS_SUPPORTED;

    private static Server server;

    private static ContextHandler context;

    private static File docroot;

    private static File otherroot;

    private static final AtomicBoolean allowAliases = new AtomicBoolean(false);

    private static final AtomicBoolean allowSymlinks = new AtomicBoolean(false);

    @Test
    public void testBadPath() throws Exception {
        final String path = "bad";
        try {
            ContextHandlerGetResourceTest.context.getResource(path);
            Assertions.fail(("Expected " + (MalformedURLException.class)));
        } catch (MalformedURLException e) {
        }
        try {
            ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
            Assertions.fail(("Expected " + (MalformedURLException.class)));
        } catch (MalformedURLException e) {
        }
    }

    @Test
    public void testGetUnknown() throws Exception {
        final String path = "/unknown.txt";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals("unknown.txt", resource.getFile().getName());
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile());
        Assertions.assertFalse(resource.exists());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertNull(url);
    }

    @Test
    public void testGetUnknownDir() throws Exception {
        final String path = "/unknown/";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals("unknown", resource.getFile().getName());
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile());
        Assertions.assertFalse(resource.exists());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertNull(url);
    }

    @Test
    public void testRoot() throws Exception {
        final String path = "/";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile());
        Assertions.assertTrue(resource.exists());
        Assertions.assertTrue(resource.isDirectory());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, new File(url.toURI()));
    }

    @Test
    public void testSubdir() throws Exception {
        final String path = "/subdir";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile());
        Assertions.assertTrue(resource.exists());
        Assertions.assertTrue(resource.isDirectory());
        Assertions.assertTrue(resource.toString().endsWith("/"));
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, new File(url.toURI()).getParentFile());
    }

    @Test
    public void testSubdirSlash() throws Exception {
        final String path = "/subdir/";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile());
        Assertions.assertTrue(resource.exists());
        Assertions.assertTrue(resource.isDirectory());
        Assertions.assertTrue(resource.toString().endsWith("/"));
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, new File(url.toURI()).getParentFile());
    }

    @Test
    public void testGetKnown() throws Exception {
        final String path = "/index.html";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals("index.html", resource.getFile().getName());
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile());
        Assertions.assertTrue(resource.exists());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, new File(url.toURI()).getParentFile());
    }

    @Test
    public void testNormalize() throws Exception {
        final String path = "/down/.././index.html";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals("index.html", resource.getFile().getName());
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile());
        Assertions.assertTrue(resource.exists());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, new File(url.toURI()).getParentFile());
    }

    @Test
    public void testTooNormal() throws Exception {
        final String path = "/down/.././../";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertNull(resource);
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertNull(url);
    }

    @Test
    public void testDeep() throws Exception {
        final String path = "/subdir/data.txt";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals("data.txt", resource.getFile().getName());
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile().getParentFile());
        Assertions.assertTrue(resource.exists());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, new File(url.toURI()).getParentFile().getParentFile());
    }

    @Test
    public void testEncodedSlash() throws Exception {
        final String path = "/subdir%2Fdata.txt";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals("subdir%2Fdata.txt", resource.getFile().getName());
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile());
        Assertions.assertFalse(resource.exists());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertNull(url);
    }

    @Test
    public void testEncodedSlosh() throws Exception {
        final String path = "/subdir%5Cdata.txt";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals("subdir%5Cdata.txt", resource.getFile().getName());
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile());
        Assertions.assertFalse(resource.exists());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertNull(url);
    }

    @Test
    public void testEncodedNull() throws Exception {
        final String path = "/subdir/data.txt%00";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertEquals("data.txt%00", resource.getFile().getName());
        Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile().getParentFile());
        Assertions.assertFalse(resource.exists());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertNull(url);
    }

    @Test
    public void testSlashSlash() throws Exception {
        File expected = new File(ContextHandlerGetResourceTest.docroot, FS.separators("subdir/data.txt"));
        URL expectedUrl = expected.toURI().toURL();
        String path = "//subdir/data.txt";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        MatcherAssert.assertThat(("Resource: " + resource), resource, Matchers.nullValue());
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        MatcherAssert.assertThat(("Resource: " + url), url, Matchers.nullValue());
        path = "/subdir//data.txt";
        resource = ContextHandlerGetResourceTest.context.getResource(path);
        MatcherAssert.assertThat(("Resource: " + resource), resource, Matchers.nullValue());
        url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        MatcherAssert.assertThat(("Resource: " + url), url, Matchers.nullValue());
    }

    @Test
    public void testAliasedFile() throws Exception {
        Assumptions.assumeTrue(ContextHandlerGetResourceTest.OS_ALIAS_SUPPORTED, "OS Supports 8.3 Aliased / Alternate References");
        final String path = "/subdir/TEXTFI~1.TXT";
        Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
        Assertions.assertNull(resource);
        URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
        Assertions.assertNull(url);
    }

    @Test
    public void testAliasedFileAllowed() throws Exception {
        Assumptions.assumeTrue(ContextHandlerGetResourceTest.OS_ALIAS_SUPPORTED, "OS Supports 8.3 Aliased / Alternate References");
        try {
            ContextHandlerGetResourceTest.allowAliases.set(true);
            final String path = "/subdir/TEXTFI~1.TXT";
            Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
            Assertions.assertNotNull(resource);
            Assertions.assertEquals(ContextHandlerGetResourceTest.context.getResource("/subdir/TextFile.Long.txt").getURI(), resource.getAlias());
            URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
            Assertions.assertNotNull(url);
            Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, new File(url.toURI()).getParentFile().getParentFile());
        } finally {
            ContextHandlerGetResourceTest.allowAliases.set(false);
        }
    }

    @Test
    @EnabledOnOs({ OS.LINUX, OS.MAC })
    public void testSymlinkKnown() throws Exception {
        try {
            ContextHandlerGetResourceTest.allowSymlinks.set(true);
            final String path = "/other/other.txt";
            Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
            Assertions.assertNotNull(resource);
            Assertions.assertEquals("other.txt", resource.getFile().getName());
            Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile().getParentFile());
            Assertions.assertTrue(resource.exists());
            URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
            Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, new File(url.toURI()).getParentFile().getParentFile());
        } finally {
            ContextHandlerGetResourceTest.allowSymlinks.set(false);
        }
    }

    @Test
    @EnabledOnOs({ OS.LINUX, OS.MAC })
    public void testSymlinkNested() throws Exception {
        try {
            ContextHandlerGetResourceTest.allowSymlinks.set(true);
            final String path = "/web/logs/file.log";
            Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
            Assertions.assertNotNull(resource);
            Assertions.assertEquals("file.log", resource.getFile().getName());
            Assertions.assertTrue(resource.exists());
        } finally {
            ContextHandlerGetResourceTest.allowSymlinks.set(false);
        }
    }

    @Test
    @EnabledOnOs({ OS.LINUX, OS.MAC })
    public void testSymlinkUnknown() throws Exception {
        try {
            ContextHandlerGetResourceTest.allowSymlinks.set(true);
            final String path = "/other/unknown.txt";
            Resource resource = ContextHandlerGetResourceTest.context.getResource(path);
            Assertions.assertNotNull(resource);
            Assertions.assertEquals("unknown.txt", resource.getFile().getName());
            Assertions.assertEquals(ContextHandlerGetResourceTest.docroot, resource.getFile().getParentFile().getParentFile());
            Assertions.assertFalse(resource.exists());
            URL url = ContextHandlerGetResourceTest.context.getServletContext().getResource(path);
            Assertions.assertNull(url);
        } finally {
            ContextHandlerGetResourceTest.allowSymlinks.set(false);
        }
    }
}

