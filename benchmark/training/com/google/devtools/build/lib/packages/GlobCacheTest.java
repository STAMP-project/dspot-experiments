/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.packages;


import com.google.devtools.build.lib.packages.Globber.BadGlobException;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.util.Pair;
import com.google.devtools.build.lib.vfs.Path;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GlobCache}
 */
@RunWith(JUnit4.class)
public class GlobCacheTest {
    private static final List<String> NONE = Collections.emptyList();

    private Scratch scratch = new Scratch("/workspace");

    private Path packageDirectory;

    private Path buildFile;

    private GlobCache cache;

    @Test
    public void testSafeGlob() throws Exception {
        List<Path> paths = cache.safeGlobUnsorted("*.js", false).get();
        assertPathsAre(paths, "/workspace/isolated/first.js", "/workspace/isolated/second.js");
    }

    @Test
    public void testSafeGlobInvalidPattern() throws Exception {
        String invalidPattern = "Foo?.txt";
        try {
            cache.safeGlobUnsorted(invalidPattern, false);
            Assert.fail((("Expected pattern " + invalidPattern) + " to fail"));
        } catch (BadGlobException expected) {
        }
    }

    @Test
    public void testGetGlob() throws Exception {
        List<String> glob = cache.getGlobUnsorted("*.js");
        assertThat(glob).containsExactly("first.js", "second.js");
    }

    @Test
    public void testGetGlob_subdirectory() throws Exception {
        List<String> glob = cache.getGlobUnsorted("foo/*.js");
        assertThat(glob).containsExactly("foo/first.js", "foo/second.js");
    }

    @Test
    public void testGetKeySet() throws Exception {
        assertThat(cache.getKeySet()).isEmpty();
        cache.getGlobUnsorted("*.java");
        assertThat(cache.getKeySet()).containsExactly(Pair.of("*.java", false));
        cache.getGlobUnsorted("*.java");
        assertThat(cache.getKeySet()).containsExactly(Pair.of("*.java", false));
        cache.getGlobUnsorted("*.js");
        assertThat(cache.getKeySet()).containsExactly(Pair.of("*.java", false), Pair.of("*.js", false));
        cache.getGlobUnsorted("*.java", true);
        assertThat(cache.getKeySet()).containsExactly(Pair.of("*.java", false), Pair.of("*.js", false), Pair.of("*.java", true));
        try {
            cache.getGlobUnsorted("invalid?");
            Assert.fail("Expected an invalid regex exception");
        } catch (BadGlobException expected) {
        }
        assertThat(cache.getKeySet()).containsExactly(Pair.of("*.java", false), Pair.of("*.js", false), Pair.of("*.java", true));
        cache.getGlobUnsorted("foo/first.*");
        assertThat(cache.getKeySet()).containsExactly(Pair.of("*.java", false), Pair.of("*.java", true), Pair.of("*.js", false), Pair.of("foo/first.*", false));
    }

    @Test
    public void testGlob() throws Exception {
        assertEmpty(cache.globUnsorted(list("*.java"), GlobCacheTest.NONE, false));
        assertThat(cache.globUnsorted(list("*.*"), GlobCacheTest.NONE, false)).containsExactly("first.js", "first.txt", "second.js", "second.txt");
        assertThat(cache.globUnsorted(list("*.*"), list("first.js"), false)).containsExactly("first.txt", "second.js", "second.txt");
        assertThat(cache.globUnsorted(list("*.txt", "first.*"), GlobCacheTest.NONE, false)).containsExactly("first.txt", "second.txt", "first.js");
    }

    @Test
    public void testRecursiveGlobDoesNotMatchSubpackage() throws Exception {
        List<String> glob = cache.getGlobUnsorted("**/*.js");
        assertThat(glob).containsExactly("first.js", "second.js", "foo/first.js", "bar/first.js", "foo/second.js", "bar/second.js");
    }

    @Test
    public void testSingleFileExclude_Star() throws Exception {
        assertThat(cache.globUnsorted(list("*"), list("first.txt"), false)).containsExactly("BUILD", "bar", "first.js", "foo", "second.js", "second.txt");
    }

    @Test
    public void testSingleFileExclude_StarStar() throws Exception {
        assertThat(cache.globUnsorted(list("**"), list("first.txt"), false)).containsExactly("BUILD", "bar", "bar/first.js", "bar/second.js", "first.js", "foo", "foo/first.js", "foo/second.js", "second.js", "second.txt");
    }

    @Test
    public void testExcludeAll_Star() throws Exception {
        assertThat(cache.globUnsorted(list("*"), list("*"), false)).isEmpty();
    }

    @Test
    public void testExcludeAll_Star_NoMatchesAnyway() throws Exception {
        assertThat(cache.globUnsorted(list("nope"), list("*"), false)).isEmpty();
    }

    @Test
    public void testExcludeAll_StarStar() throws Exception {
        assertThat(cache.globUnsorted(list("**"), list("**"), false)).isEmpty();
    }

    @Test
    public void testExcludeAll_Manual() throws Exception {
        assertThat(cache.globUnsorted(list("**"), list("*", "*/*", "*/*/*"), false)).isEmpty();
    }

    @Test
    public void testSingleFileExcludeDoesntMatch() throws Exception {
        assertThat(cache.globUnsorted(list("first.txt"), list("nope.txt"), false)).containsExactly("first.txt");
    }

    @Test
    public void testExcludeDirectory() throws Exception {
        assertThat(cache.globUnsorted(list("foo/*"), GlobCacheTest.NONE, true)).containsExactly("foo/first.js", "foo/second.js");
        assertThat(cache.globUnsorted(list("foo/*"), list("foo"), false)).containsExactly("foo/first.js", "foo/second.js");
    }

    @Test
    public void testChildGlobWithChildExclude() throws Exception {
        assertThat(cache.globUnsorted(list("foo/*"), list("foo/*"), false)).isEmpty();
        assertThat(cache.globUnsorted(list("foo/first.js", "foo/second.js"), list("foo/*"), false)).isEmpty();
        assertThat(cache.globUnsorted(list("foo/first.js"), list("foo/first.js"), false)).isEmpty();
        assertThat(cache.globUnsorted(list("foo/first.js"), list("*/first.js"), false)).isEmpty();
        assertThat(cache.globUnsorted(list("foo/first.js"), list("*/*"), false)).isEmpty();
    }
}

