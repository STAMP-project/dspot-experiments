/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.storage.contrib.nio;


import UnixPath.ROOT_PATH;
import com.google.common.testing.NullPointerTester;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link UnixPath}.
 */
@RunWith(JUnit4.class)
public class UnixPathTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNormalize() {
        assertThat(UnixPathTest.p(".").normalize()).isEqualTo(UnixPathTest.p(""));
        assertThat(UnixPathTest.p("/").normalize()).isEqualTo(UnixPathTest.p("/"));
        assertThat(UnixPathTest.p("/.").normalize()).isEqualTo(UnixPathTest.p("/"));
        assertThat(UnixPathTest.p("/a/b/../c").normalize()).isEqualTo(UnixPathTest.p("/a/c"));
        assertThat(UnixPathTest.p("/a/b/./c").normalize()).isEqualTo(UnixPathTest.p("/a/b/c"));
        assertThat(UnixPathTest.p("a/b/../c").normalize()).isEqualTo(UnixPathTest.p("a/c"));
        assertThat(UnixPathTest.p("a/b/./c").normalize()).isEqualTo(UnixPathTest.p("a/b/c"));
        assertThat(UnixPathTest.p("/a/b/../../c").normalize()).isEqualTo(UnixPathTest.p("/c"));
        assertThat(UnixPathTest.p("/a/b/./.././.././c").normalize()).isEqualTo(UnixPathTest.p("/c"));
    }

    @Test
    public void testNormalize_empty_returnsEmpty() {
        assertThat(UnixPathTest.p("").normalize()).isEqualTo(UnixPathTest.p(""));
    }

    @Test
    public void testNormalize_underflow_isAllowed() {
        assertThat(UnixPathTest.p("../").normalize()).isEqualTo(UnixPathTest.p(""));
    }

    @Test
    public void testNormalize_extraSlashes_getRemoved() {
        assertThat(UnixPathTest.p("///").normalize()).isEqualTo(UnixPathTest.p("/"));
        assertThat(UnixPathTest.p("/hi//there").normalize()).isEqualTo(UnixPathTest.p("/hi/there"));
        assertThat(UnixPathTest.p("/hi////.///there").normalize()).isEqualTo(UnixPathTest.p("/hi/there"));
    }

    @Test
    public void testNormalize_trailingSlash() {
        assertThat(UnixPathTest.p("/hi/there/").normalize()).isEqualTo(UnixPathTest.p("/hi/there/"));
        assertThat(UnixPathTest.p("/hi/there/../").normalize()).isEqualTo(UnixPathTest.p("/hi/"));
        assertThat(UnixPathTest.p("/hi/there/..").normalize()).isEqualTo(UnixPathTest.p("/hi/"));
        assertThat(UnixPathTest.p("hi/../").normalize()).isEqualTo(UnixPathTest.p(""));
        assertThat(UnixPathTest.p("/hi/../").normalize()).isEqualTo(UnixPathTest.p("/"));
        assertThat(UnixPathTest.p("hi/..").normalize()).isEqualTo(UnixPathTest.p(""));
        assertThat(UnixPathTest.p("/hi/..").normalize()).isEqualTo(UnixPathTest.p("/"));
    }

    @Test
    public void testNormalize_sameObjectOptimization() {
        UnixPath path = UnixPathTest.p("/hi/there");
        assertThat(path.normalize()).isSameAs(path);
        path = UnixPathTest.p("/hi/there/");
        assertThat(path.normalize()).isSameAs(path);
    }

    @Test
    public void testResolve() {
        assertThat(UnixPathTest.p("/hello").resolve(UnixPathTest.p("cat"))).isEqualTo(UnixPathTest.p("/hello/cat"));
        assertThat(UnixPathTest.p("/hello/").resolve(UnixPathTest.p("cat"))).isEqualTo(UnixPathTest.p("/hello/cat"));
        assertThat(UnixPathTest.p("hello/").resolve(UnixPathTest.p("cat"))).isEqualTo(UnixPathTest.p("hello/cat"));
        assertThat(UnixPathTest.p("hello/").resolve(UnixPathTest.p("cat/"))).isEqualTo(UnixPathTest.p("hello/cat/"));
        assertThat(UnixPathTest.p("hello/").resolve(UnixPathTest.p(""))).isEqualTo(UnixPathTest.p("hello/"));
        assertThat(UnixPathTest.p("hello/").resolve(UnixPathTest.p("/hi/there"))).isEqualTo(UnixPathTest.p("/hi/there"));
    }

    @Test
    public void testResolve_sameObjectOptimization() {
        UnixPath path = UnixPathTest.p("/hi/there");
        assertThat(path.resolve(UnixPathTest.p(""))).isSameAs(path);
        assertThat(UnixPathTest.p("hello").resolve(path)).isSameAs(path);
    }

    @Test
    public void testGetPath() {
        assertThat(UnixPath.getPath(false, "hello")).isEqualTo(UnixPathTest.p("hello"));
        assertThat(UnixPath.getPath(false, "hello", "cat")).isEqualTo(UnixPathTest.p("hello/cat"));
        assertThat(UnixPath.getPath(false, "/hello", "cat")).isEqualTo(UnixPathTest.p("/hello/cat"));
        assertThat(UnixPath.getPath(false, "/hello", "cat", "inc.")).isEqualTo(UnixPathTest.p("/hello/cat/inc."));
        assertThat(UnixPath.getPath(false, "hello/", "/hi/there")).isEqualTo(UnixPathTest.p("/hi/there"));
    }

    @Test
    public void testResolveSibling() {
        assertThat(UnixPathTest.p("/hello/cat").resolveSibling(UnixPathTest.p("dog"))).isEqualTo(UnixPathTest.p("/hello/dog"));
        assertThat(UnixPathTest.p("/").resolveSibling(UnixPathTest.p("dog"))).isEqualTo(UnixPathTest.p("dog"));
    }

    @Test
    public void testResolveSibling_preservesTrailingSlash() {
        assertThat(UnixPathTest.p("/hello/cat").resolveSibling(UnixPathTest.p("dog/"))).isEqualTo(UnixPathTest.p("/hello/dog/"));
        assertThat(UnixPathTest.p("/").resolveSibling(UnixPathTest.p("dog/"))).isEqualTo(UnixPathTest.p("dog/"));
    }

    @Test
    public void testRelativize() {
        assertThat(UnixPathTest.p("/foo/bar/hop/dog").relativize(UnixPathTest.p("/foo/mop/top"))).isEqualTo(UnixPathTest.p("../../../mop/top"));
        assertThat(UnixPathTest.p("/foo/bar/dog").relativize(UnixPathTest.p("/foo/mop/top"))).isEqualTo(UnixPathTest.p("../../mop/top"));
        assertThat(UnixPathTest.p("/foo/bar/hop/dog").relativize(UnixPathTest.p("/foo/mop/top/../../mog"))).isEqualTo(UnixPathTest.p("../../../mop/top/../../mog"));
        assertThat(UnixPathTest.p("/foo/bar/hop/dog").relativize(UnixPathTest.p("/foo/../mog"))).isEqualTo(UnixPathTest.p("../../../../mog"));
        assertThat(UnixPathTest.p("").relativize(UnixPathTest.p("foo/mop/top/"))).isEqualTo(UnixPathTest.p("foo/mop/top/"));
    }

    @Test
    public void testRelativize_absoluteMismatch_notAllowed() {
        thrown.expect(IllegalArgumentException.class);
        UnixPathTest.p("/a/b/").relativize(UnixPathTest.p(""));
    }

    @Test
    public void testRelativize_preservesTrailingSlash() {
        // This behavior actually diverges from sun.nio.fs.UnixPath:
        // bsh % print(Paths.get("/a/b/").relativize(Paths.get("/etc/")));
        // ../../etc
        assertThat(UnixPathTest.p("/foo/bar/hop/dog").relativize(UnixPathTest.p("/foo/../mog/"))).isEqualTo(UnixPathTest.p("../../../../mog/"));
        assertThat(UnixPathTest.p("/a/b/").relativize(UnixPathTest.p("/etc/"))).isEqualTo(UnixPathTest.p("../../etc/"));
    }

    @Test
    public void testStartsWith() {
        assertThat(UnixPathTest.p("/hi/there").startsWith(UnixPathTest.p("/hi/there"))).isTrue();
        assertThat(UnixPathTest.p("/hi/there").startsWith(UnixPathTest.p("/hi/therf"))).isFalse();
        assertThat(UnixPathTest.p("/hi/there").startsWith(UnixPathTest.p("/hi"))).isTrue();
        assertThat(UnixPathTest.p("/hi/there").startsWith(UnixPathTest.p("/hi/"))).isTrue();
        assertThat(UnixPathTest.p("/hi/there").startsWith(UnixPathTest.p("hi"))).isFalse();
        assertThat(UnixPathTest.p("/hi/there").startsWith(UnixPathTest.p("/"))).isTrue();
        assertThat(UnixPathTest.p("/hi/there").startsWith(UnixPathTest.p(""))).isFalse();
        assertThat(UnixPathTest.p("/a/b").startsWith(UnixPathTest.p("a/b/"))).isFalse();
        assertThat(UnixPathTest.p("/a/b/").startsWith(UnixPathTest.p("a/b/"))).isFalse();
        assertThat(UnixPathTest.p("/hi/there").startsWith(UnixPathTest.p(""))).isFalse();
        assertThat(UnixPathTest.p("").startsWith(UnixPathTest.p(""))).isTrue();
    }

    @Test
    public void testStartsWith_comparesComponentsIndividually() {
        assertThat(UnixPathTest.p("/hello").startsWith(UnixPathTest.p("/hell"))).isFalse();
        assertThat(UnixPathTest.p("/hello").startsWith(UnixPathTest.p("/hello"))).isTrue();
    }

    @Test
    public void testEndsWith() {
        assertThat(UnixPathTest.p("/hi/there").endsWith(UnixPathTest.p("there"))).isTrue();
        assertThat(UnixPathTest.p("/hi/there").endsWith(UnixPathTest.p("therf"))).isFalse();
        assertThat(UnixPathTest.p("/hi/there").endsWith(UnixPathTest.p("/blag/therf"))).isFalse();
        assertThat(UnixPathTest.p("/hi/there").endsWith(UnixPathTest.p("/hi/there"))).isTrue();
        assertThat(UnixPathTest.p("/hi/there").endsWith(UnixPathTest.p("/there"))).isFalse();
        assertThat(UnixPathTest.p("/human/that/you/cry").endsWith(UnixPathTest.p("that/you/cry"))).isTrue();
        assertThat(UnixPathTest.p("/human/that/you/cry").endsWith(UnixPathTest.p("that/you/cry/"))).isTrue();
        assertThat(UnixPathTest.p("/hi/there/").endsWith(UnixPathTest.p("/"))).isFalse();
        assertThat(UnixPathTest.p("/hi/there").endsWith(UnixPathTest.p(""))).isFalse();
        assertThat(UnixPathTest.p("").endsWith(UnixPathTest.p(""))).isTrue();
    }

    @Test
    public void testEndsWith_comparesComponentsIndividually() {
        assertThat(UnixPathTest.p("/hello").endsWith(UnixPathTest.p("lo"))).isFalse();
        assertThat(UnixPathTest.p("/hello").endsWith(UnixPathTest.p("hello"))).isTrue();
    }

    @Test
    public void testGetParent() {
        assertThat(UnixPathTest.p("").getParent()).isNull();
        assertThat(UnixPathTest.p("/").getParent()).isNull();
        assertThat(UnixPathTest.p("aaa/").getParent()).isNull();
        assertThat(UnixPathTest.p("aaa").getParent()).isNull();
        assertThat(UnixPathTest.p("/aaa/").getParent()).isEqualTo(UnixPathTest.p("/"));
        assertThat(UnixPathTest.p("a/b/c").getParent()).isEqualTo(UnixPathTest.p("a/b/"));
        assertThat(UnixPathTest.p("a/b/c/").getParent()).isEqualTo(UnixPathTest.p("a/b/"));
        assertThat(UnixPathTest.p("a/b/").getParent()).isEqualTo(UnixPathTest.p("a/"));
    }

    @Test
    public void testGetRoot() {
        assertThat(UnixPathTest.p("/hello").getRoot()).isEqualTo(UnixPathTest.p("/"));
        assertThat(UnixPathTest.p("hello").getRoot()).isNull();
    }

    @Test
    public void testGetFileName() {
        assertThat(UnixPathTest.p("").getFileName()).isEqualTo(UnixPathTest.p(""));
        assertThat(UnixPathTest.p("/").getFileName()).isNull();
        assertThat(UnixPathTest.p("/dark").getFileName()).isEqualTo(UnixPathTest.p("dark"));
        assertThat(UnixPathTest.p("/angels/").getFileName()).isEqualTo(UnixPathTest.p("angels"));
    }

    @Test
    public void testEquals() {
        assertThat(UnixPathTest.p("/a/").equals(UnixPathTest.p("/a/"))).isTrue();
        assertThat(UnixPathTest.p("/a/").equals(UnixPathTest.p("/b/"))).isFalse();
        assertThat(UnixPathTest.p("/b/").equals(UnixPathTest.p("/b"))).isFalse();
        assertThat(UnixPathTest.p("/b").equals(UnixPathTest.p("/b/"))).isFalse();
        assertThat(UnixPathTest.p("b").equals(UnixPathTest.p("/b"))).isFalse();
        assertThat(UnixPathTest.p("b").equals(UnixPathTest.p("b"))).isTrue();
    }

    @Test
    public void testSplit() {
        assertThat(UnixPathTest.p("").split().hasNext()).isFalse();
        assertThat(UnixPathTest.p("hi/there").split().hasNext()).isTrue();
        assertThat(UnixPathTest.p(UnixPathTest.p("hi/there").split().next())).isEqualTo(UnixPathTest.p("hi"));
    }

    @Test
    public void testToAbsolute() {
        assertThat(UnixPathTest.p("lol").toAbsolutePath(ROOT_PATH)).isEqualTo(UnixPathTest.p("/lol"));
        assertThat(UnixPathTest.p("lol/cat").toAbsolutePath(ROOT_PATH)).isEqualTo(UnixPathTest.p("/lol/cat"));
    }

    @Test
    public void testToAbsolute_withCurrentDirectory() {
        assertThat(UnixPathTest.p("cat").toAbsolutePath(UnixPathTest.p("/lol"))).isEqualTo(UnixPathTest.p("/lol/cat"));
        assertThat(UnixPathTest.p("cat").toAbsolutePath(UnixPathTest.p("/lol/"))).isEqualTo(UnixPathTest.p("/lol/cat"));
        assertThat(UnixPathTest.p("/hi/there").toAbsolutePath(UnixPathTest.p("/lol"))).isEqualTo(UnixPathTest.p("/hi/there"));
    }

    @Test
    public void testToAbsolute_preservesTrailingSlash() {
        assertThat(UnixPathTest.p("cat/").toAbsolutePath(UnixPathTest.p("/lol"))).isEqualTo(UnixPathTest.p("/lol/cat/"));
    }

    @Test
    public void testSubpath() {
        assertThat(UnixPathTest.p("/eins/zwei/drei/vier").subpath(0, 1)).isEqualTo(UnixPathTest.p("eins"));
        assertThat(UnixPathTest.p("/eins/zwei/drei/vier").subpath(0, 2)).isEqualTo(UnixPathTest.p("eins/zwei"));
        assertThat(UnixPathTest.p("eins/zwei/drei/vier/").subpath(1, 4)).isEqualTo(UnixPathTest.p("zwei/drei/vier"));
        assertThat(UnixPathTest.p("eins/zwei/drei/vier/").subpath(2, 4)).isEqualTo(UnixPathTest.p("drei/vier"));
    }

    @Test
    public void testSubpath_empty_returnsEmpty() {
        assertThat(UnixPathTest.p("").subpath(0, 1)).isEqualTo(UnixPathTest.p(""));
    }

    @Test
    public void testSubpath_root_throwsIae() {
        thrown.expect(IllegalArgumentException.class);
        UnixPathTest.p("/").subpath(0, 1);
    }

    @Test
    public void testSubpath_negativeIndex_throwsIae() {
        thrown.expect(IllegalArgumentException.class);
        UnixPathTest.p("/eins/zwei/drei/vier").subpath((-1), 1);
    }

    @Test
    public void testSubpath_notEnoughElements_throwsIae() {
        thrown.expect(IllegalArgumentException.class);
        UnixPathTest.p("/eins/zwei/drei/vier").subpath(0, 5);
    }

    @Test
    public void testSubpath_beginAboveEnd_throwsIae() {
        thrown.expect(IllegalArgumentException.class);
        UnixPathTest.p("/eins/zwei/drei/vier").subpath(1, 0);
    }

    @Test
    public void testSubpath_beginAndEndEqual_throwsIae() {
        thrown.expect(IllegalArgumentException.class);
        UnixPathTest.p("/eins/zwei/drei/vier").subpath(0, 0);
    }

    @Test
    public void testNameCount() {
        assertThat(UnixPathTest.p("").getNameCount()).isEqualTo(1);
        assertThat(UnixPathTest.p("/").getNameCount()).isEqualTo(0);
        assertThat(UnixPathTest.p("/hi/").getNameCount()).isEqualTo(1);
        assertThat(UnixPathTest.p("/hi/yo").getNameCount()).isEqualTo(2);
        assertThat(UnixPathTest.p("hi/yo").getNameCount()).isEqualTo(2);
    }

    @Test
    public void testNameCount_dontPermitEmptyComponents_emptiesGetIgnored() {
        assertThat(UnixPathTest.p("hi//yo").getNameCount()).isEqualTo(2);
        assertThat(UnixPathTest.p("//hi//yo//").getNameCount()).isEqualTo(2);
    }

    @Test
    public void testNameCount_permitEmptyComponents_emptiesGetCounted() {
        assertThat(UnixPathTest.pp("hi//yo").getNameCount()).isEqualTo(3);
        assertThat(UnixPathTest.pp("hi//yo/").getNameCount()).isEqualTo(4);
        assertThat(UnixPathTest.pp("hi//yo//").getNameCount()).isEqualTo(5);
    }

    @Test
    public void testNameCount_permitEmptyComponents_rootComponentDoesntCount() {
        assertThat(UnixPathTest.pp("hi/yo").getNameCount()).isEqualTo(2);
        assertThat(UnixPathTest.pp("/hi/yo").getNameCount()).isEqualTo(2);
        assertThat(UnixPathTest.pp("//hi/yo").getNameCount()).isEqualTo(3);
    }

    @Test
    public void testGetName() {
        assertThat(UnixPathTest.p("").getName(0)).isEqualTo(UnixPathTest.p(""));
        assertThat(UnixPathTest.p("/hi").getName(0)).isEqualTo(UnixPathTest.p("hi"));
        assertThat(UnixPathTest.p("hi/there").getName(1)).isEqualTo(UnixPathTest.p("there"));
    }

    @Test
    public void testCompareTo() {
        assertThat(UnixPathTest.p("/hi/there").compareTo(UnixPathTest.p("/hi/there"))).isEqualTo(0);
        assertThat(UnixPathTest.p("/hi/there").compareTo(UnixPathTest.p("/hi/therf"))).isEqualTo((-1));
        assertThat(UnixPathTest.p("/hi/there").compareTo(UnixPathTest.p("/hi/therd"))).isEqualTo(1);
    }

    @Test
    public void testCompareTo_dontPermitEmptyComponents_emptiesGetIgnored() {
        assertThat(UnixPathTest.p("a/b").compareTo(UnixPathTest.p("a//b"))).isEqualTo(0);
    }

    @Test
    public void testCompareTo_permitEmptyComponents_behaviorChanges() {
        assertThat(UnixPathTest.p("a/b").compareTo(UnixPathTest.pp("a//b"))).isEqualTo(1);
        assertThat(UnixPathTest.pp("a/b").compareTo(UnixPathTest.pp("a//b"))).isEqualTo(1);
    }

    @Test
    public void testCompareTo_comparesComponentsIndividually() {
        Assume.assumeTrue(('.' < '/'));
        assertThat("hi./there".compareTo("hi/there")).isEqualTo((-1));
        assertThat("hi.".compareTo("hi")).isEqualTo(1);
        assertThat(UnixPathTest.p("hi./there").compareTo(UnixPathTest.p("hi/there"))).isEqualTo(1);
        assertThat(UnixPathTest.p("hi./there").compareTo(UnixPathTest.p("hi/there"))).isEqualTo(1);
        Assume.assumeTrue(('0' > '/'));
        assertThat("hi0/there".compareTo("hi/there")).isEqualTo(1);
        assertThat("hi0".compareTo("hi")).isEqualTo(1);
        assertThat(UnixPathTest.p("hi0/there").compareTo(UnixPathTest.p("hi/there"))).isEqualTo(1);
    }

    @Test
    public void testSeemsLikeADirectory() {
        assertThat(UnixPathTest.p("a").seemsLikeADirectory()).isFalse();
        assertThat(UnixPathTest.p("a.").seemsLikeADirectory()).isFalse();
        assertThat(UnixPathTest.p("a..").seemsLikeADirectory()).isFalse();
        assertThat(UnixPathTest.p("").seemsLikeADirectory()).isTrue();
        assertThat(UnixPathTest.p("/").seemsLikeADirectory()).isTrue();
        assertThat(UnixPathTest.p(".").seemsLikeADirectory()).isTrue();
        assertThat(UnixPathTest.p("/.").seemsLikeADirectory()).isTrue();
        assertThat(UnixPathTest.p("..").seemsLikeADirectory()).isTrue();
        assertThat(UnixPathTest.p("/..").seemsLikeADirectory()).isTrue();
    }

    @Test
    public void testEquals_equalsTester() {
        new com.google.common.testing.EqualsTester().addEqualityGroup(UnixPathTest.p("/lol"), UnixPathTest.p("/lol")).addEqualityGroup(UnixPathTest.p("/lol//"), UnixPathTest.p("/lol//")).addEqualityGroup(UnixPathTest.p("dust")).testEquals();
    }

    @Test
    public void testNullness() throws Exception {
        NullPointerTester tester = new NullPointerTester();
        tester.ignore(UnixPath.class.getMethod("equals", Object.class));
        tester.testAllPublicStaticMethods(UnixPath.class);
        tester.testAllPublicInstanceMethods(UnixPathTest.p("solo"));
    }
}

