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
package com.google.devtools.build.lib.vfs;


import com.google.common.collect.Lists;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link UnixGlob} recursive globs.
 */
@RunWith(JUnit4.class)
public class RecursiveGlobTest {
    private Path tmpPath;

    private FileSystem fileSystem;

    @Test
    public void testDoubleStar() throws Exception {
        assertGlobMatches("**", ".", "foo", "foo/bar", "foo/bar/wiz", "foo/baz", "foo/baz/quip", "foo/baz/quip/wiz", "foo/baz/wiz", "foo/bar/wiz/file", "food", "food/baz", "food/baz/wiz", "fool", "fool/baz", "fool/baz/wiz");
    }

    @Test
    public void testDoubleDoubleStar() throws Exception {
        assertGlobMatches("**/**", ".", "foo", "foo/bar", "foo/bar/wiz", "foo/baz", "foo/baz/quip", "foo/baz/quip/wiz", "foo/baz/wiz", "foo/bar/wiz/file", "food", "food/baz", "food/baz/wiz", "fool", "fool/baz", "fool/baz/wiz");
    }

    @Test
    public void testDirectoryWithDoubleStar() throws Exception {
        assertGlobMatches("foo/**", "foo", "foo/bar", "foo/bar/wiz", "foo/baz", "foo/baz/quip", "foo/baz/quip/wiz", "foo/baz/wiz", "foo/bar/wiz/file");
    }

    @Test
    public void testIllegalPatterns() throws Exception {
        for (String prefix : Lists.newArrayList("", "*/", "**/", "ba/")) {
            String suffix = ("/" + prefix).substring(0, prefix.length());
            for (String pattern : Lists.newArrayList("**fo", "fo**", "**fo**", "fo**fo", "fo**fo**fo")) {
                assertIllegalWildcard((prefix + pattern));
                assertIllegalWildcard((pattern + suffix));
            }
        }
    }

    @Test
    public void testDoubleStarPatternWithNamedChild() throws Exception {
        assertGlobMatches("**/bar", "foo/bar");
    }

    @Test
    public void testDoubleStarPatternWithChildGlob() throws Exception {
        assertGlobMatches("**/ba*", "foo/bar", "foo/baz", "food/baz", "fool/baz");
    }

    @Test
    public void testDoubleStarAsChildGlob() throws Exception {
        assertGlobMatches("foo/**/wiz", "foo/bar/wiz", "foo/baz/quip/wiz", "foo/baz/wiz");
    }

    @Test
    public void testDoubleStarUnderNonexistentDirectory() throws Exception {
        /* => nothing */
        assertGlobMatches("not-there/**");
    }

    @Test
    public void testDoubleStarGlobWithNonExistentBase() throws Exception {
        Collection<Path> globResult = addPattern("**").globInterruptible();
        assertThat(globResult).isEmpty();
    }

    @Test
    public void testDoubleStarUnderFile() throws Exception {
        /* => nothing */
        assertGlobMatches("foo/bar/wiz/file/**");
    }

    @Test
    public void testRecursiveGlobsAreOptimized() throws Exception {
        long numGlobTasks = addPattern("**").setExcludeDirectories(false).globInterruptibleAndReturnNumGlobTasksForTesting();
        // The old glob implementation used to use 41 total glob tasks.
        // Yes, checking for an exact value here is super brittle, but it lets us catch performance
        // regressions. In other words, if you're a developer reading this comment because this test
        // case is failing, you should be very sure you know what you're doing before you change the
        // expectation of the test.
        assertThat(numGlobTasks).isEqualTo(28);
    }
}

