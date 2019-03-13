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
package org.eclipse.jetty.util.resource;


import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.zip.ZipFile;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.IO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JarResourceTest {
    private String testResURI = MavenTestingUtils.getTestResourcesPath().toUri().toASCIIString();

    @Test
    public void testJarFile() throws Exception {
        String s = ("jar:" + (testResURI)) + "TestData/test.zip!/subdir/";
        Resource r = Resource.newResource(s);
        Set<String> entries = new java.util.HashSet(Arrays.asList(r.list()));
        MatcherAssert.assertThat(entries, Matchers.containsInAnyOrder("alphabet", "numbers", "subsubdir/"));
        File extract = File.createTempFile("extract", null);
        if (extract.exists())
            extract.delete();

        extract.mkdir();
        extract.deleteOnExit();
        r.copyTo(extract);
        Resource e = Resource.newResource(extract.getAbsolutePath());
        entries = new java.util.HashSet(Arrays.asList(e.list()));
        MatcherAssert.assertThat(entries, Matchers.containsInAnyOrder("alphabet", "numbers", "subsubdir/"));
        IO.delete(extract);
        s = ("jar:" + (testResURI)) + "TestData/test.zip!/subdir/subsubdir/";
        r = Resource.newResource(s);
        entries = new java.util.HashSet(Arrays.asList(r.list()));
        MatcherAssert.assertThat(entries, Matchers.containsInAnyOrder("alphabet", "numbers"));
        extract = File.createTempFile("extract", null);
        if (extract.exists())
            extract.delete();

        extract.mkdir();
        extract.deleteOnExit();
        r.copyTo(extract);
        e = Resource.newResource(extract.getAbsolutePath());
        entries = new java.util.HashSet(Arrays.asList(e.list()));
        MatcherAssert.assertThat(entries, Matchers.containsInAnyOrder("alphabet", "numbers"));
        IO.delete(extract);
    }

    /* ------------------------------------------------------------ */
    @Test
    public void testJarFileGetAllResoures() throws Exception {
        String s = ("jar:" + (testResURI)) + "TestData/test.zip!/subdir/";
        Resource r = Resource.newResource(s);
        Collection<Resource> deep = r.getAllResources();
        Assertions.assertEquals(4, deep.size());
    }

    @Test
    public void testJarFileIsContainedIn() throws Exception {
        String s = ("jar:" + (testResURI)) + "TestData/test.zip!/subdir/";
        Resource r = Resource.newResource(s);
        Resource container = Resource.newResource(((testResURI) + "TestData/test.zip"));
        Assertions.assertTrue((r instanceof JarFileResource));
        JarFileResource jarFileResource = ((JarFileResource) (r));
        Assertions.assertTrue(jarFileResource.isContainedIn(container));
        container = Resource.newResource(((testResURI) + "TestData"));
        Assertions.assertFalse(jarFileResource.isContainedIn(container));
    }

    /* ------------------------------------------------------------ */
    @Test
    public void testJarFileLastModified() throws Exception {
        String s = ("jar:" + (testResURI)) + "TestData/test.zip!/subdir/numbers";
        try (ZipFile zf = new ZipFile(MavenTestingUtils.getTestResourceFile("TestData/test.zip"))) {
            long last = zf.getEntry("subdir/numbers").getTime();
            Resource r = Resource.newResource(s);
            Assertions.assertEquals(last, r.lastModified());
        }
    }

    /* ------------------------------------------------------------ */
    @Test
    public void testJarFileCopyToDirectoryTraversal() throws Exception {
        String s = ("jar:" + (testResURI)) + "TestData/extract.zip!/";
        Resource r = Resource.newResource(s);
        Assertions.assertTrue((r instanceof JarResource));
        JarResource jarResource = ((JarResource) (r));
        File destParent = File.createTempFile("copyjar", null);
        if (destParent.exists())
            destParent.delete();

        destParent.mkdir();
        destParent.deleteOnExit();
        File dest = new File(((destParent.getCanonicalPath()) + "/extract"));
        if (dest.exists())
            dest.delete();

        dest.mkdir();
        dest.deleteOnExit();
        jarResource.copyTo(dest);
        // dest contains only the valid entry; dest.getParent() contains only the dest directory
        Assertions.assertEquals(1, dest.listFiles().length);
        Assertions.assertEquals(1, dest.getParentFile().listFiles().length);
        FilenameFilter dotdotFilenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File directory, String name) {
                return name.equals("dotdot.txt");
            }
        };
        Assertions.assertEquals(0, dest.listFiles(dotdotFilenameFilter).length);
        Assertions.assertEquals(0, dest.getParentFile().listFiles(dotdotFilenameFilter).length);
        FilenameFilter extractfileFilenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File directory, String name) {
                return name.equals("extract-filenotdir");
            }
        };
        Assertions.assertEquals(0, dest.listFiles(extractfileFilenameFilter).length);
        Assertions.assertEquals(0, dest.getParentFile().listFiles(extractfileFilenameFilter).length);
        FilenameFilter currentDirectoryFilenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File directory, String name) {
                return name.equals("current.txt");
            }
        };
        Assertions.assertEquals(1, dest.listFiles(currentDirectoryFilenameFilter).length);
        Assertions.assertEquals(0, dest.getParentFile().listFiles(currentDirectoryFilenameFilter).length);
        IO.delete(dest);
        Assertions.assertFalse(dest.exists());
    }

    @Test
    public void testEncodedFileName() throws Exception {
        String s = ("jar:" + (testResURI)) + "TestData/test.zip!/file%20name.txt";
        Resource r = Resource.newResource(s);
        Assertions.assertTrue(r.exists());
    }
}

