/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.util;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class FileUtilTest extends Assert {
    @Test
    public void testNormalizePath() {
        if (FileUtil.isWindows()) {
            Assert.assertEquals("foo\\bar", FileUtil.normalizePath("foo/bar"));
            Assert.assertEquals("foo\\bar\\baz", FileUtil.normalizePath("foo/bar\\baz"));
            Assert.assertEquals("movefile\\sub\\sub2\\.done\\goodday.txt", FileUtil.normalizePath("movefile/sub/sub2\\.done\\goodday.txt"));
        } else {
            Assert.assertEquals("foo/bar", FileUtil.normalizePath("foo/bar"));
            Assert.assertEquals("foo/bar/baz", FileUtil.normalizePath("foo/bar\\baz"));
            Assert.assertEquals("movefile/sub/sub2/.done/goodday.txt", FileUtil.normalizePath("movefile/sub/sub2\\.done\\goodday.txt"));
        }
    }

    @Test
    public void testStripLeadingSeparator() {
        Assert.assertEquals(null, FileUtil.stripLeadingSeparator(null));
        Assert.assertEquals("foo", FileUtil.stripLeadingSeparator("foo"));
        Assert.assertEquals("foo/bar", FileUtil.stripLeadingSeparator("foo/bar"));
        Assert.assertEquals("foo/", FileUtil.stripLeadingSeparator("foo/"));
        Assert.assertEquals("foo/bar", FileUtil.stripLeadingSeparator("/foo/bar"));
        Assert.assertEquals("foo/bar", FileUtil.stripLeadingSeparator("//foo/bar"));
        Assert.assertEquals("foo/bar", FileUtil.stripLeadingSeparator("///foo/bar"));
    }

    @Test
    public void testHasLeadingSeparator() {
        Assert.assertEquals(false, FileUtil.hasLeadingSeparator(null));
        Assert.assertEquals(false, FileUtil.hasLeadingSeparator("foo"));
        Assert.assertEquals(false, FileUtil.hasLeadingSeparator("foo/bar"));
        Assert.assertEquals(false, FileUtil.hasLeadingSeparator("foo/"));
        Assert.assertEquals(true, FileUtil.hasLeadingSeparator("/foo/bar"));
        Assert.assertEquals(true, FileUtil.hasLeadingSeparator("//foo/bar"));
        Assert.assertEquals(true, FileUtil.hasLeadingSeparator("///foo/bar"));
    }

    @Test
    public void testStripFirstLeadingSeparator() {
        Assert.assertEquals(null, FileUtil.stripFirstLeadingSeparator(null));
        Assert.assertEquals("foo", FileUtil.stripFirstLeadingSeparator("foo"));
        Assert.assertEquals("foo/bar", FileUtil.stripFirstLeadingSeparator("foo/bar"));
        Assert.assertEquals("foo/", FileUtil.stripFirstLeadingSeparator("foo/"));
        Assert.assertEquals("foo/bar", FileUtil.stripFirstLeadingSeparator("/foo/bar"));
        Assert.assertEquals("/foo/bar", FileUtil.stripFirstLeadingSeparator("//foo/bar"));
        Assert.assertEquals("//foo/bar", FileUtil.stripFirstLeadingSeparator("///foo/bar"));
    }

    @Test
    public void testStripTrailingSeparator() {
        Assert.assertEquals(null, FileUtil.stripTrailingSeparator(null));
        Assert.assertEquals("foo", FileUtil.stripTrailingSeparator("foo"));
        Assert.assertEquals("foo/bar", FileUtil.stripTrailingSeparator("foo/bar"));
        Assert.assertEquals("foo", FileUtil.stripTrailingSeparator("foo/"));
        Assert.assertEquals("foo/bar", FileUtil.stripTrailingSeparator("foo/bar/"));
        Assert.assertEquals("/foo/bar", FileUtil.stripTrailingSeparator("/foo/bar"));
        Assert.assertEquals("/foo/bar", FileUtil.stripTrailingSeparator("/foo/bar/"));
        Assert.assertEquals("/foo/bar", FileUtil.stripTrailingSeparator("/foo/bar//"));
        Assert.assertEquals("/foo/bar", FileUtil.stripTrailingSeparator("/foo/bar///"));
        Assert.assertEquals("/foo", FileUtil.stripTrailingSeparator("/foo"));
        Assert.assertEquals("/foo", FileUtil.stripTrailingSeparator("/foo/"));
        Assert.assertEquals("/", FileUtil.stripTrailingSeparator("/"));
        Assert.assertEquals("//", FileUtil.stripTrailingSeparator("//"));
    }

    @Test
    public void testStripPath() {
        Assert.assertEquals(null, FileUtil.stripPath(null));
        Assert.assertEquals("", FileUtil.stripPath("/"));
        Assert.assertEquals("foo.xml", FileUtil.stripPath("/foo.xml"));
        Assert.assertEquals("foo", FileUtil.stripPath("foo"));
        Assert.assertEquals("bar", FileUtil.stripPath("foo/bar"));
        Assert.assertEquals("bar", FileUtil.stripPath("/foo/bar"));
    }

    @Test
    public void testStripPathWithMixedSeparators() {
        Assert.assertEquals(null, FileUtil.stripPath(null));
        Assert.assertEquals("", FileUtil.stripPath("/"));
        Assert.assertEquals("foo.xml", FileUtil.stripPath("/foo.xml"));
        Assert.assertEquals("foo", FileUtil.stripPath("foo"));
        Assert.assertEquals("baz", FileUtil.stripPath("foo/bar\\baz"));
        Assert.assertEquals("bar", FileUtil.stripPath("\\foo\\bar"));
        Assert.assertEquals("baz", FileUtil.stripPath("/foo\\bar/baz"));
    }

    @Test
    public void testStripExt() {
        Assert.assertEquals(null, FileUtil.stripExt(null));
        Assert.assertEquals("foo", FileUtil.stripExt("foo"));
        Assert.assertEquals("foo", FileUtil.stripExt("foo.xml"));
        Assert.assertEquals("/foo/bar", FileUtil.stripExt("/foo/bar.xml"));
    }

    @Test
    public void testOnlyExt() {
        Assert.assertEquals(null, FileUtil.onlyExt(null));
        Assert.assertEquals(null, FileUtil.onlyExt("foo"));
        Assert.assertEquals("xml", FileUtil.onlyExt("foo.xml"));
        Assert.assertEquals("xml", FileUtil.onlyExt("/foo/bar.xml"));
        Assert.assertEquals("tar.gz", FileUtil.onlyExt("/foo/bigfile.tar.gz"));
        Assert.assertEquals("tar.gz", FileUtil.onlyExt("/foo.bar/bigfile.tar.gz"));
    }

    @Test
    public void testOnlyPath() {
        Assert.assertEquals(null, FileUtil.onlyPath(null));
        Assert.assertEquals(null, FileUtil.onlyPath("foo"));
        Assert.assertEquals(null, FileUtil.onlyPath("foo.xml"));
        Assert.assertEquals("foo", FileUtil.onlyPath("foo/bar.xml"));
        Assert.assertEquals("/foo", FileUtil.onlyPath("/foo/bar.xml"));
        Assert.assertEquals("/foo/bar", FileUtil.onlyPath("/foo/bar/baz.xml"));
        Assert.assertEquals("/", FileUtil.onlyPath("/foo.xml"));
        Assert.assertEquals("/bar", FileUtil.onlyPath("/bar/foo.xml"));
    }

    @Test
    public void testOnlyPathWithMixedSeparators() {
        Assert.assertEquals(null, FileUtil.onlyPath(null));
        Assert.assertEquals(null, FileUtil.onlyPath("foo"));
        Assert.assertEquals(null, FileUtil.onlyPath("foo.xml"));
        Assert.assertEquals("foo", FileUtil.onlyPath("foo/bar.xml"));
        Assert.assertEquals("/foo", FileUtil.onlyPath("/foo\\bar.xml"));
        Assert.assertEquals("\\foo\\bar", FileUtil.onlyPath("\\foo\\bar/baz.xml"));
        Assert.assertEquals("\\", FileUtil.onlyPath("\\foo.xml"));
        Assert.assertEquals("/bar", FileUtil.onlyPath("/bar\\foo.xml"));
    }

    @Test
    public void testCompactPath() {
        Assert.assertEquals(null, FileUtil.compactPath(null));
        if (FileUtil.isWindows()) {
            Assert.assertEquals("..\\foo", FileUtil.compactPath("..\\foo"));
            Assert.assertEquals("..\\..\\foo", FileUtil.compactPath("..\\..\\foo"));
            Assert.assertEquals("..\\..\\foo\\bar", FileUtil.compactPath("..\\..\\foo\\bar"));
            Assert.assertEquals("..\\..\\foo", FileUtil.compactPath("..\\..\\foo\\bar\\.."));
            Assert.assertEquals("foo", FileUtil.compactPath("foo"));
            Assert.assertEquals("bar", FileUtil.compactPath("foo\\..\\bar"));
            Assert.assertEquals("bar\\baz", FileUtil.compactPath("foo\\..\\bar\\baz"));
            Assert.assertEquals("foo\\baz", FileUtil.compactPath("foo\\bar\\..\\baz"));
            Assert.assertEquals("baz", FileUtil.compactPath("foo\\bar\\..\\..\\baz"));
            Assert.assertEquals("..\\baz", FileUtil.compactPath("foo\\bar\\..\\..\\..\\baz"));
            Assert.assertEquals("..\\foo\\bar", FileUtil.compactPath("..\\foo\\bar"));
            Assert.assertEquals("foo\\bar\\baz", FileUtil.compactPath("foo\\bar\\.\\baz"));
            Assert.assertEquals("foo\\bar\\baz", FileUtil.compactPath("foo\\bar\\\\baz"));
            Assert.assertEquals("\\foo\\bar\\baz", FileUtil.compactPath("\\foo\\bar\\baz"));
            Assert.assertEquals("\\", FileUtil.compactPath("\\"));
            Assert.assertEquals("\\", FileUtil.compactPath("/"));
            Assert.assertEquals("/", FileUtil.compactPath("\\", '/'));
            Assert.assertEquals("/", FileUtil.compactPath("/", '/'));
        } else {
            Assert.assertEquals("../foo", FileUtil.compactPath("../foo"));
            Assert.assertEquals("../../foo", FileUtil.compactPath("../../foo"));
            Assert.assertEquals("../../foo/bar", FileUtil.compactPath("../../foo/bar"));
            Assert.assertEquals("../../foo", FileUtil.compactPath("../../foo/bar/.."));
            Assert.assertEquals("foo", FileUtil.compactPath("foo"));
            Assert.assertEquals("bar", FileUtil.compactPath("foo/../bar"));
            Assert.assertEquals("bar/baz", FileUtil.compactPath("foo/../bar/baz"));
            Assert.assertEquals("foo/baz", FileUtil.compactPath("foo/bar/../baz"));
            Assert.assertEquals("baz", FileUtil.compactPath("foo/bar/../../baz"));
            Assert.assertEquals("../baz", FileUtil.compactPath("foo/bar/../../../baz"));
            Assert.assertEquals("../foo/bar", FileUtil.compactPath("../foo/bar"));
            Assert.assertEquals("foo/bar/baz", FileUtil.compactPath("foo/bar/./baz"));
            Assert.assertEquals("foo/bar/baz", FileUtil.compactPath("foo/bar//baz"));
            Assert.assertEquals("/foo/bar/baz", FileUtil.compactPath("/foo/bar/baz"));
            Assert.assertEquals("/", FileUtil.compactPath("/"));
            Assert.assertEquals("/", FileUtil.compactPath("\\"));
            Assert.assertEquals("/", FileUtil.compactPath("/", '/'));
            Assert.assertEquals("/", FileUtil.compactPath("\\", '/'));
        }
    }

    @Test
    public void testCompactWindowsStylePath() {
        String path = "E:\\workspace\\foo\\bar\\some-thing\\.\\target\\processes\\2";
        String expected = "E:\\workspace\\foo\\bar\\some-thing\\target\\processes\\2";
        Assert.assertEquals(expected, FileUtil.compactPath(path, '\\'));
    }

    @Test
    public void testCompactPathSeparator() {
        Assert.assertEquals(null, FileUtil.compactPath(null, '\''));
        Assert.assertEquals("..\\foo", FileUtil.compactPath("..\\foo", '\\'));
        Assert.assertEquals("../foo", FileUtil.compactPath("../foo", '/'));
        Assert.assertEquals("../foo/bar", FileUtil.compactPath("../foo\\bar", '/'));
        Assert.assertEquals("..\\foo\\bar", FileUtil.compactPath("../foo\\bar", '\\'));
    }

    @Test
    public void testDefaultTempFileSuffixAndPrefix() throws Exception {
        File tmp = FileUtil.createTempFile("tmp-", ".tmp", new File("target/tmp"));
        Assert.assertNotNull(tmp);
        Assert.assertTrue("Should be a file", tmp.isFile());
    }

    @Test
    public void testDefaultTempFile() throws Exception {
        File tmp = FileUtil.createTempFile(null, null, new File("target/tmp"));
        Assert.assertNotNull(tmp);
        Assert.assertTrue("Should be a file", tmp.isFile());
    }

    @Test
    public void testDefaultTempFileParent() throws Exception {
        File tmp = FileUtil.createTempFile(null, null, new File("target"));
        Assert.assertNotNull(tmp);
        Assert.assertTrue("Should be a file", tmp.isFile());
    }

    @Test
    public void testCreateNewFile() throws Exception {
        File file = new File("target/data/foo.txt");
        if (file.exists()) {
            FileUtil.deleteFile(file);
        }
        Assert.assertFalse(("File should not exist " + file), file.exists());
        Assert.assertTrue(("A new file should be created " + file), FileUtil.createNewFile(file));
    }

    @Test
    public void testRenameUsingDelete() throws Exception {
        File file = new File("target/data/foo.txt");
        if (!(file.exists())) {
            FileUtil.createNewFile(file);
        }
        File target = new File("target/bar.txt");
        FileUtil.renameFileUsingCopy(file, target);
        Assert.assertTrue("File not copied", target.exists());
        Assert.assertFalse("File not deleted", file.exists());
    }
}

