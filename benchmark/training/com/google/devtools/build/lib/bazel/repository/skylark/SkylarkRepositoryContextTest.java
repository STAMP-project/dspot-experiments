/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.bazel.repository.skylark;


import Type.STRING;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.packages.Attribute;
import com.google.devtools.build.lib.rules.repository.RepositoryFunction.RepositoryFunctionException;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.Root;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for complex function of SkylarkRepositoryContext.
 */
@RunWith(JUnit4.class)
public class SkylarkRepositoryContextTest {
    private Scratch scratch;

    private Path outputDirectory;

    private Root root;

    private Path workspaceFile;

    private SkylarkRepositoryContext context;

    @Test
    public void testAttr() throws Exception {
        setUpContextForRule(ImmutableMap.<String, Object>of("name", "test", "foo", "bar"), Attribute.attr("foo", STRING).build());
        assertThat(context.getAttr().getFieldNames()).contains("foo");
        assertThat(context.getAttr().getValue("foo")).isEqualTo("bar");
    }

    @Test
    public void testWhich() throws Exception {
        setUpContexForRule("test");
        SkylarkRepositoryContext.setPathEnvironment("/bin", "/path/sbin", ".");
        scratch.file("/bin/true").setExecutable(true);
        scratch.file("/path/sbin/true").setExecutable(true);
        scratch.file("/path/sbin/false").setExecutable(true);
        scratch.file("/path/bin/undef").setExecutable(true);
        scratch.file("/path/bin/def").setExecutable(true);
        scratch.file("/bin/undef");
        assertThat(context.which("anything", null)).isNull();
        assertThat(context.which("def", null)).isNull();
        assertThat(context.which("undef", null)).isNull();
        assertThat(context.which("true", null).toString()).isEqualTo("/bin/true");
        assertThat(context.which("false", null).toString()).isEqualTo("/path/sbin/false");
    }

    @Test
    public void testFile() throws Exception {
        setUpContexForRule("test");
        context.createFile(context.path("foobar"), "", true, null);
        context.createFile(context.path("foo/bar"), "foobar", true, null);
        context.createFile(context.path("bar/foo/bar"), "", true, null);
        testOutputFile(outputDirectory.getChild("foobar"), "");
        testOutputFile(outputDirectory.getRelative("foo/bar"), "foobar");
        testOutputFile(outputDirectory.getRelative("bar/foo/bar"), "");
        try {
            context.createFile(context.path("/absolute"), "", true, null);
            Assert.fail("Expected error on creating path outside of the repository directory");
        } catch (RepositoryFunctionException ex) {
            assertThat(ex).hasCauseThat().hasMessageThat().isEqualTo("Cannot write outside of the repository directory for path /absolute");
        }
        try {
            context.createFile(context.path("../somepath"), "", true, null);
            Assert.fail("Expected error on creating path outside of the repository directory");
        } catch (RepositoryFunctionException ex) {
            assertThat(ex).hasCauseThat().hasMessageThat().isEqualTo("Cannot write outside of the repository directory for path /somepath");
        }
        try {
            context.createFile(context.path("foo/../../somepath"), "", true, null);
            Assert.fail("Expected error on creating path outside of the repository directory");
        } catch (RepositoryFunctionException ex) {
            assertThat(ex).hasCauseThat().hasMessageThat().isEqualTo("Cannot write outside of the repository directory for path /somepath");
        }
    }

    @Test
    public void testSymlink() throws Exception {
        setUpContexForRule("test");
        context.createFile(context.path("foo"), "foobar", true, null);
        context.symlink(context.path("foo"), context.path("bar"), null);
        testOutputFile(outputDirectory.getChild("bar"), "foobar");
        assertThat(context.path("bar").realpath()).isEqualTo(context.path("foo"));
    }

    @Test
    public void testDirectoryListing() throws Exception {
        setUpContexForRule("test");
        scratch.file("/my/folder/a");
        scratch.file("/my/folder/b");
        scratch.file("/my/folder/c");
        assertThat(context.path("/my/folder").readdir()).containsExactly(context.path("/my/folder/a"), context.path("/my/folder/b"), context.path("/my/folder/c"));
    }
}

