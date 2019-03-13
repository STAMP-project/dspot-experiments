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
package org.apache.hadoop.registry.client.binding;


import java.util.List;
import org.apache.hadoop.fs.PathNotFoundException;
import org.junit.Assert;
import org.junit.Test;


public class TestRegistryPathUtils extends Assert {
    public static final String EURO = "\u20ac";

    @Test
    public void testFormatAscii() throws Throwable {
        String in = "hostname01101101-1";
        assertConverted(in, in);
    }

    /* Euro symbol */
    @Test
    public void testFormatEuroSymbol() throws Throwable {
        assertConverted("xn--lzg", TestRegistryPathUtils.EURO);
    }

    @Test
    public void testFormatIdempotent() throws Throwable {
        assertConverted("xn--lzg", RegistryPathUtils.RegistryPathUtils.encodeForRegistry(TestRegistryPathUtils.EURO));
    }

    @Test
    public void testFormatCyrillicSpaced() throws Throwable {
        assertConverted("xn--pa 3-k4di", "\u0413PA\u0414 3");
    }

    @Test
    public void testPaths() throws Throwable {
        TestRegistryPathUtils.assertCreatedPathEquals("/", "/", "");
        TestRegistryPathUtils.assertCreatedPathEquals("/", "", "");
        TestRegistryPathUtils.assertCreatedPathEquals("/", "", "/");
        TestRegistryPathUtils.assertCreatedPathEquals("/", "/", "/");
        TestRegistryPathUtils.assertCreatedPathEquals("/a", "/a", "");
        TestRegistryPathUtils.assertCreatedPathEquals("/a", "/", "a");
        TestRegistryPathUtils.assertCreatedPathEquals("/a/b", "/a", "b");
        TestRegistryPathUtils.assertCreatedPathEquals("/a/b", "/a/", "b");
        TestRegistryPathUtils.assertCreatedPathEquals("/a/b", "/a", "/b");
        TestRegistryPathUtils.assertCreatedPathEquals("/a/b", "/a", "/b/");
        TestRegistryPathUtils.assertCreatedPathEquals("/a", "/a", "/");
        TestRegistryPathUtils.assertCreatedPathEquals("/alice", "/", "/alice");
        TestRegistryPathUtils.assertCreatedPathEquals("/alice", "/alice", "/");
    }

    @Test
    public void testGetUserFromPath() throws Exception {
        Assert.assertEquals("bob", RegistryPathUtils.RegistryPathUtils.getUsername("/registry/users/bob/services/yarn-service/test1/"));
        Assert.assertEquals("bob-dev", RegistryPathUtils.RegistryPathUtils.getUsername("/registry/users/bob-dev/services/yarn-service/test1"));
        Assert.assertEquals("bob.dev", RegistryPathUtils.RegistryPathUtils.getUsername("/registry/users/bob.dev/services/yarn-service/test1"));
    }

    @Test
    public void testComplexPaths() throws Throwable {
        TestRegistryPathUtils.assertCreatedPathEquals("/", "", "");
        TestRegistryPathUtils.assertCreatedPathEquals("/yarn/registry/users/hadoop/org-apache-hadoop", "/yarn/registry", "users/hadoop/org-apache-hadoop/");
    }

    @Test
    public void testSplittingEmpty() throws Throwable {
        Assert.assertEquals(0, split("").size());
        Assert.assertEquals(0, split("/").size());
        Assert.assertEquals(0, split("///").size());
    }

    @Test
    public void testSplitting() throws Throwable {
        Assert.assertEquals(1, split("/a").size());
        Assert.assertEquals(0, split("/").size());
        Assert.assertEquals(3, split("/a/b/c").size());
        Assert.assertEquals(3, split("/a/b/c/").size());
        Assert.assertEquals(3, split("a/b/c").size());
        Assert.assertEquals(3, split("/a/b//c").size());
        Assert.assertEquals(3, split("//a/b/c/").size());
        List<String> split = split("//a/b/c/");
        Assert.assertEquals("a", split.get(0));
        Assert.assertEquals("b", split.get(1));
        Assert.assertEquals("c", split.get(2));
    }

    @Test
    public void testParentOf() throws Throwable {
        Assert.assertEquals("/", parentOf("/a"));
        Assert.assertEquals("/", parentOf("/a/"));
        Assert.assertEquals("/a", parentOf("/a/b"));
        Assert.assertEquals("/a/b", parentOf("/a/b/c"));
    }

    @Test
    public void testLastPathEntry() throws Throwable {
        Assert.assertEquals("", lastPathEntry("/"));
        Assert.assertEquals("", lastPathEntry("//"));
        Assert.assertEquals("c", lastPathEntry("/a/b/c"));
        Assert.assertEquals("c", lastPathEntry("/a/b/c/"));
    }

    @Test(expected = PathNotFoundException.class)
    public void testParentOfRoot() throws Throwable {
        parentOf("/");
    }

    @Test
    public void testValidPaths() throws Throwable {
        assertValidPath("/");
        assertValidPath("/a/b/c");
        assertValidPath("/users/drwho/org-apache-hadoop/registry/appid-55-55");
        assertValidPath("/a50");
    }

    @Test
    public void testInvalidPaths() throws Throwable {
        assertInvalidPath("/a_b");
        assertInvalidPath("/UpperAndLowerCase");
        assertInvalidPath("/space in string");
        // Is this valid?    assertInvalidPath("/50");
    }
}

