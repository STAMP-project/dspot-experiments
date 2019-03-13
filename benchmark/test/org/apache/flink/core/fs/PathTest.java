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
package org.apache.flink.core.fs;


import java.io.IOException;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link Path} class.
 */
public class PathTest {
    @Test
    public void testPathFromString() {
        Path p = new Path("/my/path");
        Assert.assertEquals("/my/path", p.toUri().getPath());
        Assert.assertNull(p.toUri().getScheme());
        p = new Path("/my/path/");
        Assert.assertEquals("/my/path", p.toUri().getPath());
        Assert.assertNull(p.toUri().getScheme());
        p = new Path("/my//path/");
        Assert.assertEquals("/my/path", p.toUri().getPath());
        Assert.assertNull(p.toUri().getScheme());
        p = new Path("/my//path//a///");
        Assert.assertEquals("/my/path/a", p.toUri().getPath());
        Assert.assertNull(p.toUri().getScheme());
        p = new Path("\\my\\path\\\\a\\\\\\");
        Assert.assertEquals("/my/path/a", p.toUri().getPath());
        Assert.assertNull(p.toUri().getScheme());
        p = new Path("/my/path/ ");
        Assert.assertEquals("/my/path", p.toUri().getPath());
        Assert.assertNull(p.toUri().getScheme());
        p = new Path("hdfs:///my/path");
        Assert.assertEquals("/my/path", p.toUri().getPath());
        Assert.assertEquals("hdfs", p.toUri().getScheme());
        p = new Path("hdfs:///my/path/");
        Assert.assertEquals("/my/path", p.toUri().getPath());
        Assert.assertEquals("hdfs", p.toUri().getScheme());
        p = new Path("file:///my/path");
        Assert.assertEquals("/my/path", p.toUri().getPath());
        Assert.assertEquals("file", p.toUri().getScheme());
        p = new Path("C:/my/windows/path");
        Assert.assertEquals("/C:/my/windows/path", p.toUri().getPath());
        p = new Path("file:/C:/my/windows/path");
        Assert.assertEquals("/C:/my/windows/path", p.toUri().getPath());
        try {
            new Path(((String) (null)));
            Assert.fail();
        } catch (Exception e) {
            // exception expected
        }
        try {
            new Path("");
            Assert.fail();
        } catch (Exception e) {
            // exception expected
        }
        try {
            new Path(" ");
            Assert.fail();
        } catch (Exception e) {
            // exception expected
        }
    }

    @Test
    public void testIsAbsolute() {
        // UNIX
        Path p = new Path("/my/abs/path");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("/");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("./my/rel/path");
        Assert.assertFalse(p.isAbsolute());
        p = new Path("my/rel/path");
        Assert.assertFalse(p.isAbsolute());
        // WINDOWS
        p = new Path("C:/my/abs/windows/path");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("y:/my/abs/windows/path");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("/y:/my/abs/windows/path");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("b:\\my\\abs\\windows\\path");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("/c:/my/dir");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("/C:/");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("C:");
        Assert.assertFalse(p.isAbsolute());
        p = new Path("C:/");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("C:my\\relative\\path");
        Assert.assertFalse(p.isAbsolute());
        p = new Path("\\my\\dir");
        Assert.assertTrue(p.isAbsolute());
        p = new Path("\\");
        Assert.assertTrue(p.isAbsolute());
        p = new Path(".\\my\\relative\\path");
        Assert.assertFalse(p.isAbsolute());
        p = new Path("my\\relative\\path");
        Assert.assertFalse(p.isAbsolute());
        p = new Path("\\\\myServer\\myDir");
        Assert.assertTrue(p.isAbsolute());
    }

    @Test
    public void testGetName() {
        Path p = new Path("/my/fancy/path");
        Assert.assertEquals("path", p.getName());
        p = new Path("/my/fancy/path/");
        Assert.assertEquals("path", p.getName());
        p = new Path("hdfs:///my/path");
        Assert.assertEquals("path", p.getName());
        p = new Path("hdfs:///myPath/");
        Assert.assertEquals("myPath", p.getName());
        p = new Path("/");
        Assert.assertEquals("", p.getName());
        p = new Path("C:/my/windows/path");
        Assert.assertEquals("path", p.getName());
        p = new Path("file:/C:/my/windows/path");
        Assert.assertEquals("path", p.getName());
    }

    @Test
    public void testGetParent() {
        Path p = new Path("/my/fancy/path");
        Assert.assertEquals("/my/fancy", p.getParent().toUri().getPath());
        p = new Path("/my/other/fancy/path/");
        Assert.assertEquals("/my/other/fancy", p.getParent().toUri().getPath());
        p = new Path("hdfs:///my/path");
        Assert.assertEquals("/my", p.getParent().toUri().getPath());
        p = new Path("hdfs:///myPath/");
        Assert.assertEquals("/", p.getParent().toUri().getPath());
        p = new Path("/");
        Assert.assertNull(p.getParent());
        p = new Path("C:/my/windows/path");
        Assert.assertEquals("/C:/my/windows", p.getParent().toUri().getPath());
    }

    @Test
    public void testSuffix() {
        Path p = new Path("/my/path");
        p = p.suffix("_123");
        Assert.assertEquals("/my/path_123", p.toUri().getPath());
        p = new Path("/my/path/");
        p = p.suffix("/abc");
        Assert.assertEquals("/my/path/abc", p.toUri().getPath());
        p = new Path("C:/my/windows/path");
        p = p.suffix("/abc");
        Assert.assertEquals("/C:/my/windows/path/abc", p.toUri().getPath());
    }

    @Test
    public void testDepth() {
        Path p = new Path("/my/path");
        Assert.assertEquals(2, p.depth());
        p = new Path("/my/fancy/path/");
        Assert.assertEquals(3, p.depth());
        p = new Path("/my/fancy/fancy/fancy/fancy/fancy/fancy/fancy/fancy/fancy/fancy/path");
        Assert.assertEquals(12, p.depth());
        p = new Path("/");
        Assert.assertEquals(0, p.depth());
        p = new Path("C:/my/windows/path");
        Assert.assertEquals(4, p.depth());
    }

    @Test
    public void testParsing() {
        URI u;
        String scheme = "hdfs";
        String authority = "localhost:8000";
        String path = "/test/test";
        // correct usage
        // hdfs://localhost:8000/test/test
        u = new Path((((scheme + "://") + authority) + path)).toUri();
        Assert.assertEquals(scheme, u.getScheme());
        Assert.assertEquals(authority, u.getAuthority());
        Assert.assertEquals(path, u.getPath());
        // hdfs:///test/test
        u = new Path(((scheme + "://") + path)).toUri();
        Assert.assertEquals(scheme, u.getScheme());
        Assert.assertEquals(null, u.getAuthority());
        Assert.assertEquals(path, u.getPath());
        // hdfs:/test/test
        u = new Path(((scheme + ":") + path)).toUri();
        Assert.assertEquals(scheme, u.getScheme());
        Assert.assertEquals(null, u.getAuthority());
        Assert.assertEquals(path, u.getPath());
        // incorrect usage
        // hdfs://test/test
        u = new Path(((scheme + ":/") + path)).toUri();
        Assert.assertEquals(scheme, u.getScheme());
        Assert.assertEquals("test", u.getAuthority());
        Assert.assertEquals("/test", u.getPath());
        // hdfs:////test/test
        u = new Path(((scheme + ":///") + path)).toUri();
        Assert.assertEquals("hdfs", u.getScheme());
        Assert.assertEquals(null, u.getAuthority());
        Assert.assertEquals(path, u.getPath());
    }

    @Test
    public void testMakeQualified() throws IOException {
        // make relative path qualified
        String path = "test/test";
        Path p = new Path(path).makeQualified(FileSystem.getLocalFileSystem());
        URI u = p.toUri();
        Assert.assertEquals("file", u.getScheme());
        Assert.assertEquals(null, u.getAuthority());
        String q = getPath();
        Assert.assertEquals(q, u.getPath());
        // make absolute path qualified
        path = "/test/test";
        p = new Path(path).makeQualified(FileSystem.getLocalFileSystem());
        u = p.toUri();
        Assert.assertEquals("file", u.getScheme());
        Assert.assertEquals(null, u.getAuthority());
        Assert.assertEquals(path, u.getPath());
    }
}

