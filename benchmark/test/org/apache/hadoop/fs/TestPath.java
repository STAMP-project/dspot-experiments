/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.AvroTestUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;

import static Path.WINDOWS;


/**
 * Test Hadoop Filesystem Paths.
 */
public class TestPath {
    @Test(timeout = 30000)
    public void testToString() {
        toStringTest("/");
        toStringTest("/foo");
        toStringTest("/foo/bar");
        toStringTest("foo");
        toStringTest("foo/bar");
        toStringTest("/foo/bar#boo");
        toStringTest("foo/bar#boo");
        boolean emptyException = false;
        try {
            toStringTest("");
        } catch (IllegalArgumentException e) {
            // expect to receive an IllegalArgumentException
            emptyException = true;
        }
        Assert.assertTrue(emptyException);
        if (WINDOWS) {
            toStringTest("c:");
            toStringTest("c:/");
            toStringTest("c:foo");
            toStringTest("c:foo/bar");
            toStringTest("c:foo/bar");
            toStringTest("c:/foo/bar");
            toStringTest("C:/foo/bar#boo");
            toStringTest("C:foo/bar#boo");
        }
    }

    @Test(timeout = 30000)
    public void testNormalize() throws URISyntaxException {
        Assert.assertEquals("", new Path(".").toString());
        Assert.assertEquals("..", new Path("..").toString());
        Assert.assertEquals("/", new Path("/").toString());
        Assert.assertEquals("/", new Path("//").toString());
        Assert.assertEquals("/", new Path("///").toString());
        Assert.assertEquals("//foo/", new Path("//foo/").toString());
        Assert.assertEquals("//foo/", new Path("//foo//").toString());
        Assert.assertEquals("//foo/bar", new Path("//foo//bar").toString());
        Assert.assertEquals("/foo", new Path("/foo/").toString());
        Assert.assertEquals("/foo", new Path("/foo/").toString());
        Assert.assertEquals("foo", new Path("foo/").toString());
        Assert.assertEquals("foo", new Path("foo//").toString());
        Assert.assertEquals("foo/bar", new Path("foo//bar").toString());
        Assert.assertEquals("hdfs://foo/foo2/bar/baz/", new Path(new URI("hdfs://foo//foo2///bar/baz///")).toString());
        if (WINDOWS) {
            Assert.assertEquals("c:/a/b", new Path("c:\\a\\b").toString());
        }
    }

    @Test(timeout = 30000)
    public void testIsAbsolute() {
        Assert.assertTrue(new Path("/").isAbsolute());
        Assert.assertTrue(new Path("/foo").isAbsolute());
        Assert.assertFalse(new Path("foo").isAbsolute());
        Assert.assertFalse(new Path("foo/bar").isAbsolute());
        Assert.assertFalse(new Path(".").isAbsolute());
        if (WINDOWS) {
            Assert.assertTrue(new Path("c:/a/b").isAbsolute());
            Assert.assertFalse(new Path("c:a/b").isAbsolute());
        }
    }

    @Test(timeout = 30000)
    public void testParent() {
        Assert.assertEquals(new Path("/foo"), new Path("/foo/bar").getParent());
        Assert.assertEquals(new Path("foo"), new Path("foo/bar").getParent());
        Assert.assertEquals(new Path("/"), new Path("/foo").getParent());
        Assert.assertEquals(null, new Path("/").getParent());
        if (WINDOWS) {
            Assert.assertEquals(new Path("c:/"), new Path("c:/foo").getParent());
        }
    }

    @Test(timeout = 30000)
    public void testChild() {
        Assert.assertEquals(new Path("."), new Path(".", "."));
        Assert.assertEquals(new Path("/"), new Path("/", "."));
        Assert.assertEquals(new Path("/"), new Path(".", "/"));
        Assert.assertEquals(new Path("/foo"), new Path("/", "foo"));
        Assert.assertEquals(new Path("/foo/bar"), new Path("/foo", "bar"));
        Assert.assertEquals(new Path("/foo/bar/baz"), new Path("/foo/bar", "baz"));
        Assert.assertEquals(new Path("/foo/bar/baz"), new Path("/foo", "bar/baz"));
        Assert.assertEquals(new Path("foo"), new Path(".", "foo"));
        Assert.assertEquals(new Path("foo/bar"), new Path("foo", "bar"));
        Assert.assertEquals(new Path("foo/bar/baz"), new Path("foo", "bar/baz"));
        Assert.assertEquals(new Path("foo/bar/baz"), new Path("foo/bar", "baz"));
        Assert.assertEquals(new Path("/foo"), new Path("/bar", "/foo"));
        if (WINDOWS) {
            Assert.assertEquals(new Path("c:/foo"), new Path("/bar", "c:/foo"));
            Assert.assertEquals(new Path("c:/foo"), new Path("d:/bar", "c:/foo"));
        }
    }

    @Test(timeout = 30000)
    public void testPathThreeArgContructor() {
        Assert.assertEquals(new Path("foo"), new Path(null, null, "foo"));
        Assert.assertEquals(new Path("scheme:///foo"), new Path("scheme", null, "/foo"));
        Assert.assertEquals(new Path("scheme://authority/foo"), new Path("scheme", "authority", "/foo"));
        if (WINDOWS) {
            Assert.assertEquals(new Path("c:/foo/bar"), new Path(null, null, "c:/foo/bar"));
            Assert.assertEquals(new Path("c:/foo/bar"), new Path(null, null, "/c:/foo/bar"));
        } else {
            Assert.assertEquals(new Path("./a:b"), new Path(null, null, "a:b"));
        }
        // Resolution tests
        if (WINDOWS) {
            Assert.assertEquals(new Path("c:/foo/bar"), new Path("/fou", new Path(null, null, "c:/foo/bar")));
            Assert.assertEquals(new Path("c:/foo/bar"), new Path("/fou", new Path(null, null, "/c:/foo/bar")));
            Assert.assertEquals(new Path("/foo/bar"), new Path("/foo", new Path(null, null, "bar")));
        } else {
            Assert.assertEquals(new Path("/foo/bar/a:b"), new Path("/foo/bar", new Path(null, null, "a:b")));
            Assert.assertEquals(new Path("/a:b"), new Path("/foo/bar", new Path(null, null, "/a:b")));
        }
    }

    @Test(timeout = 30000)
    public void testEquals() {
        Assert.assertFalse(new Path("/").equals(new Path("/foo")));
    }

    @Test(timeout = 30000)
    public void testDots() {
        // Test Path(String)
        Assert.assertEquals(new Path("/foo/bar/baz").toString(), "/foo/bar/baz");
        Assert.assertEquals(new Path("/foo/bar", ".").toString(), "/foo/bar");
        Assert.assertEquals(new Path("/foo/bar/../baz").toString(), "/foo/baz");
        Assert.assertEquals(new Path("/foo/bar/./baz").toString(), "/foo/bar/baz");
        Assert.assertEquals(new Path("/foo/bar/baz/../../fud").toString(), "/foo/fud");
        Assert.assertEquals(new Path("/foo/bar/baz/.././../fud").toString(), "/foo/fud");
        Assert.assertEquals(new Path("../../foo/bar").toString(), "../../foo/bar");
        Assert.assertEquals(new Path(".././../foo/bar").toString(), "../../foo/bar");
        Assert.assertEquals(new Path("./foo/bar/baz").toString(), "foo/bar/baz");
        Assert.assertEquals(new Path("/foo/bar/../../baz/boo").toString(), "/baz/boo");
        Assert.assertEquals(new Path("foo/bar/").toString(), "foo/bar");
        Assert.assertEquals(new Path("foo/bar/../baz").toString(), "foo/baz");
        Assert.assertEquals(new Path("foo/bar/../../baz/boo").toString(), "baz/boo");
        // Test Path(Path,Path)
        Assert.assertEquals(new Path("/foo/bar", "baz/boo").toString(), "/foo/bar/baz/boo");
        Assert.assertEquals(new Path("foo/bar/", "baz/bud").toString(), "foo/bar/baz/bud");
        Assert.assertEquals(new Path("/foo/bar", "../../boo/bud").toString(), "/boo/bud");
        Assert.assertEquals(new Path("foo/bar", "../../boo/bud").toString(), "boo/bud");
        Assert.assertEquals(new Path(".", "boo/bud").toString(), "boo/bud");
        Assert.assertEquals(new Path("/foo/bar/baz", "../../boo/bud").toString(), "/foo/boo/bud");
        Assert.assertEquals(new Path("foo/bar/baz", "../../boo/bud").toString(), "foo/boo/bud");
        Assert.assertEquals(new Path("../../", "../../boo/bud").toString(), "../../../../boo/bud");
        Assert.assertEquals(new Path("../../foo", "../../../boo/bud").toString(), "../../../../boo/bud");
        Assert.assertEquals(new Path("../../foo/bar", "../boo/bud").toString(), "../../foo/boo/bud");
        Assert.assertEquals(new Path("foo/bar/baz", "../../..").toString(), "");
        Assert.assertEquals(new Path("foo/bar/baz", "../../../../..").toString(), "../..");
    }

    /**
     * Test that Windows paths are correctly handled
     */
    @Test(timeout = 5000)
    public void testWindowsPaths() throws IOException, URISyntaxException {
        PlatformAssumptions.assumeWindows();
        Assert.assertEquals(new Path("c:\\foo\\bar").toString(), "c:/foo/bar");
        Assert.assertEquals(new Path("c:/foo/bar").toString(), "c:/foo/bar");
        Assert.assertEquals(new Path("/c:/foo/bar").toString(), "c:/foo/bar");
        Assert.assertEquals(new Path("file://c:/foo/bar").toString(), "file://c:/foo/bar");
    }

    /**
     * Test invalid paths on Windows are correctly rejected
     */
    @Test(timeout = 5000)
    public void testInvalidWindowsPaths() throws IOException, URISyntaxException {
        PlatformAssumptions.assumeWindows();
        String[] invalidPaths = new String[]{ "hdfs:\\\\\\tmp" };
        for (String path : invalidPaths) {
            try {
                Path item = new Path(path);
                Assert.fail(("Did not throw for invalid path " + path));
            } catch (IllegalArgumentException iae) {
            }
        }
    }

    /**
     * Test Path objects created from other Path objects
     */
    @Test(timeout = 30000)
    public void testChildParentResolution() throws IOException, URISyntaxException {
        Path parent = new Path("foo1://bar1/baz1");
        Path child = new Path("foo2://bar2/baz2");
        Assert.assertEquals(child, new Path(parent, child));
    }

    @Test(timeout = 30000)
    public void testScheme() throws IOException {
        Assert.assertEquals("foo:/bar", new Path("foo:/", "/bar").toString());
        Assert.assertEquals("foo://bar/baz", new Path("foo://bar/", "/baz").toString());
    }

    @Test(timeout = 30000)
    public void testURI() throws IOException, URISyntaxException {
        URI uri = new URI("file:///bar#baz");
        Path path = new Path(uri);
        Assert.assertTrue(uri.equals(new URI(path.toString())));
        FileSystem fs = path.getFileSystem(new Configuration());
        Assert.assertTrue(uri.equals(new URI(fs.makeQualified(path).toString())));
        // uri without hash
        URI uri2 = new URI("file:///bar/baz");
        Assert.assertTrue(uri2.equals(new URI(fs.makeQualified(new Path(uri2)).toString())));
        Assert.assertEquals("foo://bar/baz#boo", new Path("foo://bar/", new Path(new URI("/baz#boo"))).toString());
        Assert.assertEquals("foo://bar/baz/fud#boo", new Path(new Path(new URI("foo://bar/baz#bud")), new Path(new URI("fud#boo"))).toString());
        // if the child uri is absolute path
        Assert.assertEquals("foo://bar/fud#boo", new Path(new Path(new URI("foo://bar/baz#bud")), new Path(new URI("/fud#boo"))).toString());
    }

    /**
     * Test URIs created from Path objects
     */
    @Test(timeout = 30000)
    public void testPathToUriConversion() throws IOException, URISyntaxException {
        // Path differs from URI in that it ignores the query part..
        Assert.assertEquals("? mark char in to URI", new URI(null, null, "/foo?bar", null, null), new Path("/foo?bar").toUri());
        Assert.assertEquals("escape slashes chars in to URI", new URI(null, null, "/foo\"bar", null, null), new Path("/foo\"bar").toUri());
        Assert.assertEquals("spaces in chars to URI", new URI(null, null, "/foo bar", null, null), new Path("/foo bar").toUri());
        // therefore "foo?bar" is a valid Path, so a URI created from a Path
        // has path "foo?bar" where in a straight URI the path part is just "foo"
        Assert.assertEquals("/foo?bar", new Path("http://localhost/foo?bar").toUri().getPath());
        Assert.assertEquals("/foo", new URI("http://localhost/foo?bar").getPath());
        // The path part handling in Path is equivalent to URI
        Assert.assertEquals(new URI("/foo;bar").getPath(), new Path("/foo;bar").toUri().getPath());
        Assert.assertEquals(new URI("/foo;bar"), new Path("/foo;bar").toUri());
        Assert.assertEquals(new URI("/foo+bar"), new Path("/foo+bar").toUri());
        Assert.assertEquals(new URI("/foo-bar"), new Path("/foo-bar").toUri());
        Assert.assertEquals(new URI("/foo=bar"), new Path("/foo=bar").toUri());
        Assert.assertEquals(new URI("/foo,bar"), new Path("/foo,bar").toUri());
    }

    /**
     * Test reserved characters in URIs (and therefore Paths)
     */
    @Test(timeout = 30000)
    public void testReservedCharacters() throws IOException, URISyntaxException {
        // URI encodes the path
        Assert.assertEquals("/foo%20bar", new URI(null, null, "/foo bar", null, null).getRawPath());
        // URI#getPath decodes the path
        Assert.assertEquals("/foo bar", new URI(null, null, "/foo bar", null, null).getPath());
        // URI#toString returns an encoded path
        Assert.assertEquals("/foo%20bar", new URI(null, null, "/foo bar", null, null).toString());
        Assert.assertEquals("/foo%20bar", new Path("/foo bar").toUri().toString());
        // Reserved chars are not encoded
        Assert.assertEquals("/foo;bar", new URI("/foo;bar").getPath());
        Assert.assertEquals("/foo;bar", new URI("/foo;bar").getRawPath());
        Assert.assertEquals("/foo+bar", new URI("/foo+bar").getPath());
        Assert.assertEquals("/foo+bar", new URI("/foo+bar").getRawPath());
        // URI#getPath decodes the path part (and URL#getPath does not decode)
        Assert.assertEquals("/foo bar", new Path("http://localhost/foo bar").toUri().getPath());
        Assert.assertEquals("/foo%20bar", new Path("http://localhost/foo bar").toUri().toURL().getPath());
        Assert.assertEquals("/foo?bar", new URI("http", "localhost", "/foo?bar", null, null).getPath());
        Assert.assertEquals("/foo%3Fbar", new URI("http", "localhost", "/foo?bar", null, null).toURL().getPath());
    }

    @Test(timeout = 30000)
    public void testMakeQualified() throws URISyntaxException {
        URI defaultUri = new URI("hdfs://host1/dir1");
        URI wd = new URI("hdfs://host2/dir2");
        // The scheme from defaultUri is used but the path part is not
        Assert.assertEquals(new Path("hdfs://host1/dir/file"), new Path("file").makeQualified(defaultUri, new Path("/dir")));
        // The defaultUri is only used if the path + wd has no scheme
        Assert.assertEquals(new Path("hdfs://host2/dir2/file"), new Path("file").makeQualified(defaultUri, new Path(wd)));
    }

    @Test(timeout = 30000)
    public void testGetName() {
        Assert.assertEquals("", new Path("/").getName());
        Assert.assertEquals("foo", new Path("foo").getName());
        Assert.assertEquals("foo", new Path("/foo").getName());
        Assert.assertEquals("foo", new Path("/foo/").getName());
        Assert.assertEquals("bar", new Path("/foo/bar").getName());
        Assert.assertEquals("bar", new Path("hdfs://host/foo/bar").getName());
    }

    @Test(timeout = 30000)
    public void testAvroReflect() throws Exception {
        AvroTestUtil.testReflect(new Path("foo"), "{\"type\":\"string\",\"java-class\":\"org.apache.hadoop.fs.Path\"}");
    }

    @Test(timeout = 30000)
    public void testGlobEscapeStatus() throws Exception {
        // This test is not meaningful on Windows where * is disallowed in file name.
        PlatformAssumptions.assumeNotWindows();
        FileSystem lfs = FileSystem.getLocal(new Configuration());
        Path testRoot = lfs.makeQualified(new Path(GenericTestUtils.getTempPath("testPathGlob")));
        lfs.delete(testRoot, true);
        lfs.mkdirs(testRoot);
        Assert.assertTrue(lfs.isDirectory(testRoot));
        lfs.setWorkingDirectory(testRoot);
        // create a couple dirs with file in them
        Path[] paths = new Path[]{ new Path(testRoot, "*/f"), new Path(testRoot, "d1/f"), new Path(testRoot, "d2/f") };
        Arrays.sort(paths);
        for (Path p : paths) {
            lfs.create(p).close();
            Assert.assertTrue(lfs.exists(p));
        }
        // try the non-globbed listStatus
        FileStatus[] stats = lfs.listStatus(new Path(testRoot, "*"));
        Assert.assertEquals(1, stats.length);
        Assert.assertEquals(new Path(testRoot, "*/f"), stats[0].getPath());
        // ensure globStatus with "*" finds all dir contents
        stats = lfs.globStatus(new Path(testRoot, "*"));
        Arrays.sort(stats);
        Path[] parentPaths = new Path[paths.length];
        for (int i = 0; i < (paths.length); i++) {
            parentPaths[i] = paths[i].getParent();
        }
        Assert.assertEquals(TestPath.mergeStatuses(parentPaths), TestPath.mergeStatuses(stats));
        // ensure that globStatus with an escaped "\*" only finds "*"
        stats = lfs.globStatus(new Path(testRoot, "\\*"));
        Assert.assertEquals(1, stats.length);
        Assert.assertEquals(new Path(testRoot, "*"), stats[0].getPath());
        // try to glob the inner file for all dirs
        stats = lfs.globStatus(new Path(testRoot, "*/f"));
        Assert.assertEquals(paths.length, stats.length);
        Assert.assertEquals(TestPath.mergeStatuses(paths), TestPath.mergeStatuses(stats));
        // try to get the inner file for only the "*" dir
        stats = lfs.globStatus(new Path(testRoot, "\\*/f"));
        Assert.assertEquals(1, stats.length);
        Assert.assertEquals(new Path(testRoot, "*/f"), stats[0].getPath());
        // try to glob all the contents of the "*" dir
        stats = lfs.globStatus(new Path(testRoot, "\\*/*"));
        Assert.assertEquals(1, stats.length);
        Assert.assertEquals(new Path(testRoot, "*/f"), stats[0].getPath());
    }

    @Test(timeout = 30000)
    public void testMergePaths() {
        Assert.assertEquals(new Path("/foo/bar"), Path.mergePaths(new Path("/foo"), new Path("/bar")));
        Assert.assertEquals(new Path("/foo/bar/baz"), Path.mergePaths(new Path("/foo/bar"), new Path("/baz")));
        Assert.assertEquals(new Path("/foo/bar/baz"), Path.mergePaths(new Path("/foo"), new Path("/bar/baz")));
        Assert.assertEquals(new Path((Shell.WINDOWS ? "/C:/foo/bar" : "/C:/foo/C:/bar")), Path.mergePaths(new Path("/C:/foo"), new Path("/C:/bar")));
        Assert.assertEquals(new Path((Shell.WINDOWS ? "/C:/bar" : "/C:/C:/bar")), Path.mergePaths(new Path("/C:/"), new Path("/C:/bar")));
        Assert.assertEquals(new Path("/bar"), Path.mergePaths(new Path("/"), new Path("/bar")));
        Assert.assertEquals(new Path("viewfs:///foo/bar"), Path.mergePaths(new Path("viewfs:///foo"), new Path("file:///bar")));
        Assert.assertEquals(new Path("viewfs://vfsauthority/foo/bar"), Path.mergePaths(new Path("viewfs://vfsauthority/foo"), new Path("file://fileauthority/bar")));
    }

    @Test(timeout = 30000)
    public void testIsWindowsAbsolutePath() {
        PlatformAssumptions.assumeWindows();
        Assert.assertTrue(Path.isWindowsAbsolutePath("C:\\test", false));
        Assert.assertTrue(Path.isWindowsAbsolutePath("C:/test", false));
        Assert.assertTrue(Path.isWindowsAbsolutePath("/C:/test", true));
        Assert.assertFalse(Path.isWindowsAbsolutePath("/test", false));
        Assert.assertFalse(Path.isWindowsAbsolutePath("/test", true));
        Assert.assertFalse(Path.isWindowsAbsolutePath("C:test", false));
        Assert.assertFalse(Path.isWindowsAbsolutePath("/C:test", true));
    }

    @Test(timeout = 30000)
    public void testSerDeser() throws Throwable {
        Path source = new Path("hdfs://localhost:4040/scratch");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(source);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            Path deser = ((Path) (ois.readObject()));
            Assert.assertEquals(source, deser);
        }
    }
}

