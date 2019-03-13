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
package org.apache.beam.sdk.extensions.gcp.storage;


import StandardResolveOptions.RESOLVE_DIRECTORY;
import StandardResolveOptions.RESOLVE_FILE;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.io.fs.ResourceIdTester;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GcsResourceId}.
 */
@RunWith(JUnit4.class)
public class GcsResourceIdTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testResolve() {
        // Tests for common gcs paths.
        Assert.assertEquals(toResourceIdentifier("gs://bucket/tmp/aa"), toResourceIdentifier("gs://bucket/tmp/").resolve("aa", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("gs://bucket/tmp/aa/bb/cc/"), toResourceIdentifier("gs://bucket/tmp/").resolve("aa", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_DIRECTORY).resolve("cc", RESOLVE_DIRECTORY));
        // Tests absolute path.
        Assert.assertEquals(toResourceIdentifier("gs://bucket/tmp/aa"), toResourceIdentifier("gs://bucket/tmp/bb/").resolve("gs://bucket/tmp/aa", RESOLVE_FILE));
        // Tests bucket with no ending '/'.
        Assert.assertEquals(toResourceIdentifier("gs://my_bucket/tmp"), toResourceIdentifier("gs://my_bucket").resolve("tmp", RESOLVE_FILE));
        // Tests path with unicode
        Assert.assertEquals(toResourceIdentifier("gs://bucket/?? ??/?? ??01.txt"), toResourceIdentifier("gs://bucket/?? ??/").resolve("?? ??01.txt", RESOLVE_FILE));
    }

    @Test
    public void testResolveHandleBadInputs() {
        Assert.assertEquals(toResourceIdentifier("gs://my_bucket/tmp/"), toResourceIdentifier("gs://my_bucket/").resolve("tmp/", RESOLVE_DIRECTORY));
    }

    @Test
    public void testResolveInvalidInputs() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The resolved file: [tmp/] should not end with '/'.");
        toResourceIdentifier("gs://my_bucket/").resolve("tmp/", RESOLVE_FILE);
    }

    @Test
    public void testResolveInvalidNotDirectory() {
        ResourceId tmpDir = toResourceIdentifier("gs://my_bucket/").resolve("tmp dir", RESOLVE_FILE);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Expected the gcsPath is a directory, but had [gs://my_bucket/tmp dir].");
        tmpDir.resolve("aa", RESOLVE_FILE);
    }

    @Test
    public void testGetCurrentDirectory() {
        // Tests gcs paths.
        Assert.assertEquals(toResourceIdentifier("gs://my_bucket/tmp dir/"), toResourceIdentifier("gs://my_bucket/tmp dir/").getCurrentDirectory());
        // Tests path with unicode.
        Assert.assertEquals(toResourceIdentifier("gs://my_bucket/?? ??/"), toResourceIdentifier("gs://my_bucket/?? ??/??01.txt").getCurrentDirectory());
        // Tests bucket with no ending '/'.
        Assert.assertEquals(toResourceIdentifier("gs://my_bucket/"), toResourceIdentifier("gs://my_bucket").getCurrentDirectory());
    }

    @Test
    public void testIsDirectory() {
        Assert.assertTrue(toResourceIdentifier("gs://my_bucket/tmp dir/").isDirectory());
        Assert.assertTrue(toResourceIdentifier("gs://my_bucket/").isDirectory());
        Assert.assertTrue(toResourceIdentifier("gs://my_bucket").isDirectory());
        Assert.assertFalse(toResourceIdentifier("gs://my_bucket/file").isDirectory());
    }

    @Test
    public void testInvalidGcsPath() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid GCS URI: gs://");
        toResourceIdentifier("gs://");
    }

    @Test
    public void testGetScheme() {
        // Tests gcs paths.
        Assert.assertEquals("gs", toResourceIdentifier("gs://my_bucket/tmp dir/").getScheme());
        // Tests bucket with no ending '/'.
        Assert.assertEquals("gs", toResourceIdentifier("gs://my_bucket").getScheme());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(toResourceIdentifier("gs://my_bucket/tmp/"), toResourceIdentifier("gs://my_bucket/tmp/"));
        Assert.assertNotEquals(toResourceIdentifier("gs://my_bucket/tmp"), toResourceIdentifier("gs://my_bucket/tmp/"));
    }

    @Test
    public void testGetFilename() {
        Assert.assertNull(toResourceIdentifier("gs://my_bucket/").getFilename());
        Assert.assertEquals("abc", toResourceIdentifier("gs://my_bucket/abc").getFilename());
        Assert.assertEquals("abc", toResourceIdentifier("gs://my_bucket/abc/").getFilename());
        Assert.assertEquals("xyz.txt", toResourceIdentifier("gs://my_bucket/abc/xyz.txt").getFilename());
    }

    @Test
    public void testResourceIdTester() {
        FileSystems.setDefaultPipelineOptions(PipelineOptionsFactory.create());
        ResourceIdTester.runResourceIdBattery(toResourceIdentifier("gs://bucket/foo/"));
    }
}

