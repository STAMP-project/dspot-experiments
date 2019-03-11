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
package org.apache.beam.sdk.io;


import StandardResolveOptions.RESOLVE_DIRECTORY;
import StandardResolveOptions.RESOLVE_FILE;
import java.io.File;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.io.fs.ResourceIdTester;
import org.apache.commons.lang3.SystemUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link LocalResourceId}.
 *
 * <p>TODO: re-enable unicode tests when BEAM-1453 is resolved.
 */
@RunWith(JUnit4.class)
public class LocalResourceIdTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testResolveInUnix() {
        if (SystemUtils.IS_OS_WINDOWS) {
            // Skip tests
            return;
        }
        // Tests for local files without the scheme.
        Assert.assertEquals(toResourceIdentifier("/root/tmp/aa"), toResourceIdentifier("/root/tmp/").resolve("aa", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("/root/tmp/aa/bb/cc/"), toResourceIdentifier("/root/tmp/").resolve("aa", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_DIRECTORY).resolve("cc", RESOLVE_DIRECTORY));
        // Tests absolute path.
        Assert.assertEquals(toResourceIdentifier("/root/tmp/aa"), toResourceIdentifier("/root/tmp/bb/").resolve("/root/tmp/aa", RESOLVE_FILE));
        // Tests empty authority and path.
        Assert.assertEquals(toResourceIdentifier("file:/aa"), toResourceIdentifier("file:///").resolve("aa", RESOLVE_FILE));
    }

    @Test
    public void testResolveNormalizationInUnix() {
        if (SystemUtils.IS_OS_WINDOWS) {
            // Skip tests
            return;
        }
        // Tests normalization of "." and ".."
        // 
        // Normalization is the implementation choice of LocalResourceId,
        // and it is not required by ResourceId.resolve().
        Assert.assertEquals(toResourceIdentifier("file://home/bb"), toResourceIdentifier("file://root/../home/output/../").resolve("aa", RESOLVE_DIRECTORY).resolve("..", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("file://root/aa/bb"), toResourceIdentifier("file://root/./").resolve("aa", RESOLVE_DIRECTORY).resolve(".", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("aa/bb"), toResourceIdentifier("a/../").resolve("aa", RESOLVE_DIRECTORY).resolve(".", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("/aa/bb"), toResourceIdentifier("/a/../").resolve("aa", RESOLVE_DIRECTORY).resolve(".", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_FILE));
        // Tests "./", "../", "~/".
        Assert.assertEquals(toResourceIdentifier("aa/bb"), toResourceIdentifier("./").resolve("aa", RESOLVE_DIRECTORY).resolve(".", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("../aa/bb"), toResourceIdentifier("../").resolve("aa", RESOLVE_DIRECTORY).resolve(".", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("~/aa/bb/"), toResourceIdentifier("~/").resolve("aa", RESOLVE_DIRECTORY).resolve(".", RESOLVE_DIRECTORY).resolve("bb", RESOLVE_DIRECTORY));
    }

    @Test
    public void testResolveHandleBadInputsInUnix() {
        if (SystemUtils.IS_OS_WINDOWS) {
            // Skip tests
            return;
        }
        Assert.assertEquals(toResourceIdentifier("/root/tmp/"), toResourceIdentifier("/root/").resolve("tmp/", RESOLVE_DIRECTORY));
    }

    @Test
    public void testResolveInvalidInputs() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The resolved file: [tmp/] should not end with '/'.");
        toResourceIdentifier("/root/").resolve("tmp/", RESOLVE_FILE);
    }

    @Test
    public void testResolveInvalidNotDirectory() {
        ResourceId tmp = toResourceIdentifier("/root/").resolve("tmp", RESOLVE_FILE);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Expected the path is a directory, but had [/root/tmp].");
        tmp.resolve("aa", RESOLVE_FILE);
    }

    @Test
    public void testResolveInWindowsOS() {
        if (!(SystemUtils.IS_OS_WINDOWS)) {
            // Skip tests
            return;
        }
        Assert.assertEquals(toResourceIdentifier("C:\\my home\\out put"), toResourceIdentifier("C:\\my home\\").resolve("out put", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("C:\\out put"), toResourceIdentifier("C:\\my home\\").resolve("..", RESOLVE_DIRECTORY).resolve(".", RESOLVE_DIRECTORY).resolve("out put", RESOLVE_FILE));
        Assert.assertEquals(toResourceIdentifier("C:\\my home\\**\\*"), toResourceIdentifier("C:\\my home\\").resolve("**", RESOLVE_DIRECTORY).resolve("*", RESOLVE_FILE));
    }

    @Test
    public void testGetCurrentDirectoryInUnix() {
        // Tests for local files without the scheme.
        Assert.assertEquals(toResourceIdentifier("/root/tmp/"), toResourceIdentifier("/root/tmp/").getCurrentDirectory());
        Assert.assertEquals(toResourceIdentifier("/"), toResourceIdentifier("/").getCurrentDirectory());
        // Tests path without parent.
        Assert.assertEquals(toResourceIdentifier("./"), toResourceIdentifier("output").getCurrentDirectory());
    }

    @Test
    public void testGetScheme() {
        // Tests for local files without the scheme.
        Assert.assertEquals("file", toResourceIdentifier("/root/tmp/").getScheme());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(toResourceIdentifier("/root/tmp/"), toResourceIdentifier("/root/tmp/"));
        Assert.assertNotEquals(toResourceIdentifier("/root/tmp"), toResourceIdentifier("/root/tmp/"));
    }

    @Test
    public void testIsDirectory() {
        Assert.assertTrue(toResourceIdentifier("/").isDirectory());
        Assert.assertTrue(toResourceIdentifier("/root/tmp/").isDirectory());
        Assert.assertFalse(toResourceIdentifier("/root").isDirectory());
    }

    @Test
    public void testToString() throws Exception {
        File someFile = tmpFolder.newFile("somefile");
        LocalResourceId fileResource = /* isDirectory */
        LocalResourceId.fromPath(someFile.toPath(), false);
        MatcherAssert.assertThat(fileResource.toString(), Matchers.not(Matchers.endsWith(File.separator)));
        MatcherAssert.assertThat(fileResource.toString(), Matchers.containsString("somefile"));
        MatcherAssert.assertThat(fileResource.toString(), Matchers.startsWith(tmpFolder.getRoot().getAbsolutePath()));
        LocalResourceId dirResource = /* isDirectory */
        LocalResourceId.fromPath(someFile.toPath(), true);
        MatcherAssert.assertThat(dirResource.toString(), Matchers.endsWith(File.separator));
        MatcherAssert.assertThat(dirResource.toString(), Matchers.containsString("somefile"));
        MatcherAssert.assertThat(dirResource.toString(), Matchers.startsWith(tmpFolder.getRoot().getAbsolutePath()));
    }

    @Test
    public void testGetFilename() {
        Assert.assertNull(toResourceIdentifier("/").getFilename());
        Assert.assertEquals("tmp", toResourceIdentifier("/root/tmp").getFilename());
        Assert.assertEquals("tmp", toResourceIdentifier("/root/tmp/").getFilename());
        Assert.assertEquals("xyz.txt", toResourceIdentifier("/root/tmp/xyz.txt").getFilename());
    }

    @Test
    public void testResourceIdTester() {
        ResourceIdTester.runResourceIdBattery(toResourceIdentifier("/tmp/foo/"));
    }
}

