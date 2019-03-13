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
package org.apache.beam.sdk.util.gcsfs;


import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of GcsPath.
 */
@RunWith(JUnit4.class)
public class GcsPathTest {
    /**
     * Test case, which tests parsing and building of GcsPaths.
     */
    static final class TestCase {
        final String uri;

        final String expectedBucket;

        final String expectedObject;

        final String[] namedComponents;

        TestCase(String uri, String... namedComponents) {
            this.uri = uri;
            this.expectedBucket = namedComponents[0];
            this.namedComponents = namedComponents;
            this.expectedObject = uri.substring(((expectedBucket.length()) + 6));
        }
    }

    // Each test case is an expected URL, then the components used to build it.
    // Empty components result in a double slash.
    static final List<GcsPathTest.TestCase> PATH_TEST_CASES = Arrays.asList(new GcsPathTest.TestCase("gs://bucket/then/object", "bucket", "then", "object"), new GcsPathTest.TestCase("gs://bucket//then/object", "bucket", "", "then", "object"), new GcsPathTest.TestCase("gs://bucket/then//object", "bucket", "then", "", "object"), new GcsPathTest.TestCase("gs://bucket/then///object", "bucket", "then", "", "", "object"), new GcsPathTest.TestCase("gs://bucket/then/object/", "bucket", "then", "object/"), new GcsPathTest.TestCase("gs://bucket/then/object/", "bucket", "then/", "object/"), new GcsPathTest.TestCase("gs://bucket/then/object//", "bucket", "then", "object", ""), new GcsPathTest.TestCase("gs://bucket/then/object//", "bucket", "then", "object/", ""), new GcsPathTest.TestCase("gs://bucket/", "bucket"));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGcsPathParsing() throws Exception {
        for (GcsPathTest.TestCase testCase : GcsPathTest.PATH_TEST_CASES) {
            String uriString = testCase.uri;
            GcsPath path = GcsPath.fromUri(URI.create(uriString));
            // Deconstruction - check bucket, object, and components.
            Assert.assertEquals(testCase.expectedBucket, path.getBucket());
            Assert.assertEquals(testCase.expectedObject, path.getObject());
            Assert.assertEquals(testCase.uri, testCase.namedComponents.length, path.getNameCount());
            // Construction - check that the path can be built from components.
            GcsPath built = GcsPath.fromComponents(null, null);
            for (String component : testCase.namedComponents) {
                built = built.resolve(component);
            }
            Assert.assertEquals(testCase.uri, built.toString());
        }
    }

    @Test
    public void testParentRelationship() throws Exception {
        GcsPath path = GcsPath.fromComponents("bucket", "then/object");
        Assert.assertEquals("bucket", path.getBucket());
        Assert.assertEquals("then/object", path.getObject());
        Assert.assertEquals(3, path.getNameCount());
        Assert.assertTrue(path.endsWith("object"));
        Assert.assertTrue(path.startsWith("bucket/then"));
        GcsPath parent = path.getParent();// gs://bucket/then/

        Assert.assertEquals("bucket", parent.getBucket());
        Assert.assertEquals("then/", parent.getObject());
        Assert.assertEquals(2, parent.getNameCount());
        Assert.assertThat(path, Matchers.not(Matchers.equalTo(parent)));
        Assert.assertTrue(path.startsWith(parent));
        Assert.assertFalse(parent.startsWith(path));
        Assert.assertTrue(parent.endsWith("then/"));
        Assert.assertTrue(parent.startsWith("bucket/then"));
        Assert.assertTrue(parent.isAbsolute());
        GcsPath root = path.getRoot();
        Assert.assertEquals(0, root.getNameCount());
        Assert.assertEquals("gs://", root.toString());
        Assert.assertEquals("", root.getBucket());
        Assert.assertEquals("", root.getObject());
        Assert.assertTrue(root.isAbsolute());
        Assert.assertThat(root, Matchers.equalTo(parent.getRoot()));
        GcsPath grandParent = parent.getParent();// gs://bucket/

        Assert.assertEquals(1, grandParent.getNameCount());
        Assert.assertEquals("gs://bucket/", grandParent.toString());
        Assert.assertTrue(grandParent.isAbsolute());
        Assert.assertThat(root, Matchers.equalTo(grandParent.getParent()));
        Assert.assertThat(root.getParent(), Matchers.nullValue());
        Assert.assertTrue(path.startsWith(path.getRoot()));
        Assert.assertTrue(parent.startsWith(path.getRoot()));
    }

    @Test
    public void testRelativeParent() throws Exception {
        GcsPath path = GcsPath.fromComponents(null, "a/b");
        GcsPath parent = path.getParent();
        Assert.assertEquals("a/", parent.toString());
        GcsPath grandParent = parent.getParent();
        Assert.assertNull(grandParent);
    }

    @Test
    public void testUriSupport() throws Exception {
        URI uri = URI.create("gs://bucket/some/path");
        GcsPath path = GcsPath.fromUri(uri);
        Assert.assertEquals("bucket", path.getBucket());
        Assert.assertEquals("some/path", path.getObject());
        URI reconstructed = path.toUri();
        Assert.assertEquals(uri, reconstructed);
        path = GcsPath.fromUri("gs://bucket");
        Assert.assertEquals("gs://bucket/", path.toString());
    }

    @Test
    public void testBucketParsing() throws Exception {
        GcsPath path = GcsPath.fromUri("gs://bucket");
        GcsPath path2 = GcsPath.fromUri("gs://bucket/");
        Assert.assertEquals(path, path2);
        Assert.assertEquals(path.toString(), path2.toString());
        Assert.assertEquals(path.toUri(), path2.toUri());
    }

    @Test
    public void testGcsPathToString() throws Exception {
        String filename = "gs://some_bucket/some/file.txt";
        GcsPath path = GcsPath.fromUri(filename);
        Assert.assertEquals(filename, path.toString());
    }

    @Test
    public void testEquals() {
        GcsPath a = GcsPath.fromComponents(null, "a/b/c");
        GcsPath a2 = GcsPath.fromComponents(null, "a/b/c");
        Assert.assertFalse(a.isAbsolute());
        Assert.assertFalse(a2.isAbsolute());
        GcsPath b = GcsPath.fromComponents("bucket", "a/b/c");
        GcsPath b2 = GcsPath.fromComponents("bucket", "a/b/c");
        Assert.assertTrue(b.isAbsolute());
        Assert.assertTrue(b2.isAbsolute());
        Assert.assertEquals(a, a);
        Assert.assertThat(a, Matchers.not(Matchers.equalTo(b)));
        Assert.assertThat(b, Matchers.not(Matchers.equalTo(a)));
        Assert.assertEquals(a, a2);
        Assert.assertEquals(a2, a);
        Assert.assertEquals(b, b2);
        Assert.assertEquals(b2, b);
        Assert.assertThat(a, Matchers.not(Matchers.equalTo(Paths.get("/tmp/foo"))));
        Assert.assertNotNull(a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidGcsPath() {
        @SuppressWarnings("unused")
        GcsPath filename = GcsPath.fromUri("file://invalid/gcs/path");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBucket() {
        GcsPath.fromComponents("invalid/", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidObject_newline() {
        GcsPath.fromComponents(null, "a\nb");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidObject_cr() {
        GcsPath.fromComponents(null, "a\rb");
    }

    @Test
    public void testResolveUri() {
        GcsPath path = GcsPath.fromComponents("bucket", "a/b/c");
        GcsPath d = path.resolve("gs://bucket2/d");
        Assert.assertEquals("gs://bucket2/d", d.toString());
    }

    @Test
    public void testResolveOther() {
        GcsPath a = GcsPath.fromComponents("bucket", "a");
        GcsPath b = a.resolve(Paths.get("b"));
        Assert.assertEquals("a/b", b.getObject());
    }

    @Test
    public void testGetFileName() {
        Assert.assertEquals("foo", GcsPath.fromUri("gs://bucket/bar/foo").getFileName().toString());
        Assert.assertEquals("foo", GcsPath.fromUri("gs://bucket/foo").getFileName().toString());
        thrown.expect(UnsupportedOperationException.class);
        GcsPath.fromUri("gs://bucket/").getFileName();
    }

    @Test
    public void testResolveSibling() {
        Assert.assertEquals("gs://bucket/bar/moo", GcsPath.fromUri("gs://bucket/bar/foo").resolveSibling("moo").toString());
        Assert.assertEquals("gs://bucket/moo", GcsPath.fromUri("gs://bucket/foo").resolveSibling("moo").toString());
        thrown.expect(UnsupportedOperationException.class);
        GcsPath.fromUri("gs://bucket/").resolveSibling("moo");
    }

    @Test
    public void testCompareTo() {
        GcsPath a = GcsPath.fromComponents("bucket", "a");
        GcsPath b = GcsPath.fromComponents("bucket", "b");
        GcsPath b2 = GcsPath.fromComponents("bucket2", "b");
        GcsPath brel = GcsPath.fromComponents(null, "b");
        GcsPath a2 = GcsPath.fromComponents("bucket", "a");
        GcsPath arel = GcsPath.fromComponents(null, "a");
        Assert.assertThat(a.compareTo(b), Matchers.lessThan(0));
        Assert.assertThat(b.compareTo(a), Matchers.greaterThan(0));
        Assert.assertThat(a.compareTo(a2), Matchers.equalTo(0));
        Assert.assertThat(a.hashCode(), Matchers.equalTo(a2.hashCode()));
        Assert.assertThat(a.hashCode(), Matchers.not(Matchers.equalTo(b.hashCode())));
        Assert.assertThat(b.hashCode(), Matchers.not(Matchers.equalTo(brel.hashCode())));
        Assert.assertThat(brel.compareTo(b), Matchers.lessThan(0));
        Assert.assertThat(b.compareTo(brel), Matchers.greaterThan(0));
        Assert.assertThat(arel.compareTo(brel), Matchers.lessThan(0));
        Assert.assertThat(brel.compareTo(arel), Matchers.greaterThan(0));
        Assert.assertThat(b.compareTo(b2), Matchers.lessThan(0));
        Assert.assertThat(b2.compareTo(b), Matchers.greaterThan(0));
    }

    @Test
    public void testCompareTo_ordering() {
        GcsPath ab = GcsPath.fromComponents("bucket", "a/b");
        GcsPath abc = GcsPath.fromComponents("bucket", "a/b/c");
        GcsPath a1b = GcsPath.fromComponents("bucket", "a-1/b");
        Assert.assertThat(ab.compareTo(a1b), Matchers.lessThan(0));
        Assert.assertThat(a1b.compareTo(ab), Matchers.greaterThan(0));
        Assert.assertThat(ab.compareTo(abc), Matchers.lessThan(0));
        Assert.assertThat(abc.compareTo(ab), Matchers.greaterThan(0));
    }

    @Test
    public void testCompareTo_buckets() {
        GcsPath a = GcsPath.fromComponents(null, "a/b/c");
        GcsPath b = GcsPath.fromComponents("bucket", "a/b/c");
        Assert.assertThat(a.compareTo(b), Matchers.lessThan(0));
        Assert.assertThat(b.compareTo(a), Matchers.greaterThan(0));
    }

    @Test
    public void testIterator() {
        GcsPath a = GcsPath.fromComponents("bucket", "a/b/c");
        Iterator<Path> it = a.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("gs://bucket/", it.next().toString());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("a", it.next().toString());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("b", it.next().toString());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("c", it.next().toString());
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testSubpath() {
        GcsPath a = GcsPath.fromComponents("bucket", "a/b/c/d");
        Assert.assertThat(a.subpath(0, 1).toString(), Matchers.equalTo("gs://bucket/"));
        Assert.assertThat(a.subpath(0, 2).toString(), Matchers.equalTo("gs://bucket/a"));
        Assert.assertThat(a.subpath(0, 3).toString(), Matchers.equalTo("gs://bucket/a/b"));
        Assert.assertThat(a.subpath(0, 4).toString(), Matchers.equalTo("gs://bucket/a/b/c"));
        Assert.assertThat(a.subpath(1, 2).toString(), Matchers.equalTo("a"));
        Assert.assertThat(a.subpath(2, 3).toString(), Matchers.equalTo("b"));
        Assert.assertThat(a.subpath(2, 4).toString(), Matchers.equalTo("b/c"));
        Assert.assertThat(a.subpath(2, 5).toString(), Matchers.equalTo("b/c/d"));
    }

    @Test
    public void testGetName() {
        GcsPath a = GcsPath.fromComponents("bucket", "a/b/c/d");
        Assert.assertEquals(5, a.getNameCount());
        Assert.assertThat(a.getName(0).toString(), Matchers.equalTo("gs://bucket/"));
        Assert.assertThat(a.getName(1).toString(), Matchers.equalTo("a"));
        Assert.assertThat(a.getName(2).toString(), Matchers.equalTo("b"));
        Assert.assertThat(a.getName(3).toString(), Matchers.equalTo("c"));
        Assert.assertThat(a.getName(4).toString(), Matchers.equalTo("d"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubPathError() {
        GcsPath a = GcsPath.fromComponents("bucket", "a/b/c/d");
        a.subpath(1, 1);// throws IllegalArgumentException

        Assert.fail();
    }
}

