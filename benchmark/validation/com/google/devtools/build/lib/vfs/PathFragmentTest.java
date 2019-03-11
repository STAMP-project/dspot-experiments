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


import PathFragment.EMPTY_FRAGMENT;
import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * This class tests the functionality of the PathFragment.
 */
@RunWith(JUnit4.class)
public class PathFragmentTest {
    @Test
    public void testEqualsAndHashCode() {
        InMemoryFileSystem filesystem = new InMemoryFileSystem();
        // A Path object.
        new EqualsTester().addEqualityGroup(PathFragment.create("../relative/path"), PathFragment.create("..").getRelative("relative").getRelative("path"), PathFragment.create(new File("../relative/path").getPath())).addEqualityGroup(PathFragment.create("something/else")).addEqualityGroup(PathFragment.create("/something/else")).addEqualityGroup(PathFragment.create("/"), PathFragment.create("//////")).addEqualityGroup(PathFragment.create(""), EMPTY_FRAGMENT).addEqualityGroup(filesystem.getPath("/")).testEquals();
    }

    @Test
    public void testHashCodeCache() {
        PathFragment relativePath = PathFragment.create("../relative/path");
        PathFragment rootPath = PathFragment.create("/");
        int oldResult = relativePath.hashCode();
        int rootResult = rootPath.hashCode();
        assertThat(relativePath.hashCode()).isEqualTo(oldResult);
        assertThat(rootPath.hashCode()).isEqualTo(rootResult);
    }

    @Test
    public void testRelativeTo() {
        assertThat(PathFragment.create("foo/bar/baz").relativeTo("foo").getPathString()).isEqualTo("bar/baz");
        assertThat(PathFragment.create("/foo/bar/baz").relativeTo("/foo").getPathString()).isEqualTo("bar/baz");
        assertThat(PathFragment.create("foo/bar/baz").relativeTo("foo/bar").getPathString()).isEqualTo("baz");
        assertThat(PathFragment.create("/foo/bar/baz").relativeTo("/foo/bar").getPathString()).isEqualTo("baz");
        assertThat(PathFragment.create("/foo").relativeTo("/").getPathString()).isEqualTo("foo");
        assertThat(PathFragment.create("foo").relativeTo("").getPathString()).isEqualTo("foo");
        assertThat(PathFragment.create("foo/bar").relativeTo("").getPathString()).isEqualTo("foo/bar");
    }

    @Test
    public void testIsAbsolute() {
        assertThat(PathFragment.create("/absolute/test").isAbsolute()).isTrue();
        assertThat(PathFragment.create("relative/test").isAbsolute()).isFalse();
        assertThat(PathFragment.create(new File("/absolute/test").getPath()).isAbsolute()).isTrue();
        assertThat(PathFragment.create(new File("relative/test").getPath()).isAbsolute()).isFalse();
    }

    @Test
    public void testIsNormalized() {
        assertThat(PathFragment.isNormalized("/absolute/path")).isTrue();
        assertThat(PathFragment.isNormalized("some//path")).isTrue();
        assertThat(PathFragment.isNormalized("some/./path")).isFalse();
        assertThat(PathFragment.isNormalized("../some/path")).isFalse();
        assertThat(PathFragment.isNormalized("./some/path")).isFalse();
        assertThat(PathFragment.isNormalized("some/path/..")).isFalse();
        assertThat(PathFragment.isNormalized("some/path/.")).isFalse();
        assertThat(PathFragment.isNormalized("some/other/../path")).isFalse();
        assertThat(PathFragment.isNormalized("some/other//tricky..path..")).isTrue();
        assertThat(PathFragment.isNormalized("/some/other//tricky..path..")).isTrue();
    }

    @Test
    public void testContainsUpLevelReferences() {
        assertThat(PathFragment.containsUplevelReferences("/absolute/path")).isFalse();
        assertThat(PathFragment.containsUplevelReferences("some//path")).isFalse();
        assertThat(PathFragment.containsUplevelReferences("some/./path")).isFalse();
        assertThat(PathFragment.containsUplevelReferences("../some/path")).isTrue();
        assertThat(PathFragment.containsUplevelReferences("./some/path")).isFalse();
        assertThat(PathFragment.containsUplevelReferences("some/path/..")).isTrue();
        assertThat(PathFragment.containsUplevelReferences("some/path/.")).isFalse();
        assertThat(PathFragment.containsUplevelReferences("some/other/../path")).isTrue();
        assertThat(PathFragment.containsUplevelReferences("some/other//tricky..path..")).isFalse();
        assertThat(PathFragment.containsUplevelReferences("/some/other//tricky..path..")).isFalse();
        // Normalization cannot remove leading uplevel references, so this will be true
        assertThat(PathFragment.create("../some/path").containsUplevelReferences()).isTrue();
        // Normalization will remove these, so no uplevel references left
        assertThat(PathFragment.create("some/path/..").containsUplevelReferences()).isFalse();
    }

    @Test
    public void testRootNodeReturnsRootString() {
        PathFragment rootFragment = PathFragment.create("/");
        assertThat(rootFragment.getPathString()).isEqualTo("/");
    }

    @Test
    public void testGetRelative() {
        assertThat(PathFragment.create("a").getRelative("b").getPathString()).isEqualTo("a/b");
        assertThat(PathFragment.create("a/b").getRelative("c/d").getPathString()).isEqualTo("a/b/c/d");
        assertThat(PathFragment.create("c/d").getRelative("/a/b").getPathString()).isEqualTo("/a/b");
        assertThat(PathFragment.create("a").getRelative("").getPathString()).isEqualTo("a");
        assertThat(PathFragment.create("/").getRelative("").getPathString()).isEqualTo("/");
        assertThat(PathFragment.create("a/b").getRelative("../foo").getPathString()).isEqualTo("a/foo");
        assertThat(PathFragment.create("/a/b").getRelative("../foo").getPathString()).isEqualTo("/a/foo");
        // Make sure any fast path of PathFragment#getRelative(PathFragment) works
        assertThat(PathFragment.create("a/b").getRelative(PathFragment.create("../foo")).getPathString()).isEqualTo("a/foo");
        assertThat(PathFragment.create("/a/b").getRelative(PathFragment.create("../foo")).getPathString()).isEqualTo("/a/foo");
        // Make sure any fast path of PathFragment#getRelative(PathFragment) works
        assertThat(PathFragment.create("c/d").getRelative(PathFragment.create("/a/b")).getPathString()).isEqualTo("/a/b");
        // Test normalization
        assertThat(PathFragment.create("a").getRelative(".").getPathString()).isEqualTo("a");
    }

    @Test
    public void testIsNormalizedRelativePath() {
        assertThat(PathFragment.isNormalizedRelativePath("/a")).isFalse();
        assertThat(PathFragment.isNormalizedRelativePath("a///b")).isFalse();
        assertThat(PathFragment.isNormalizedRelativePath("../a")).isFalse();
        assertThat(PathFragment.isNormalizedRelativePath("a/../b")).isFalse();
        assertThat(PathFragment.isNormalizedRelativePath("a/b")).isTrue();
        assertThat(PathFragment.isNormalizedRelativePath("ab")).isTrue();
    }

    @Test
    public void testContainsSeparator() {
        assertThat(PathFragment.containsSeparator("/a")).isTrue();
        assertThat(PathFragment.containsSeparator("a///b")).isTrue();
        assertThat(PathFragment.containsSeparator("../a")).isTrue();
        assertThat(PathFragment.containsSeparator("a/../b")).isTrue();
        assertThat(PathFragment.containsSeparator("a/b")).isTrue();
        assertThat(PathFragment.containsSeparator("ab")).isFalse();
    }

    @Test
    public void testGetChildWorks() {
        PathFragment pf = PathFragment.create("../some/path");
        assertThat(pf.getChild("hi")).isEqualTo(PathFragment.create("../some/path/hi"));
    }

    @Test
    public void testGetChildRejectsInvalidBaseNames() {
        PathFragment pf = PathFragment.create("../some/path");
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> pf.getChild("."));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> pf.getChild(".."));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> pf.getChild("x/y"));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> pf.getChild("/y"));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> pf.getChild("y/"));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> pf.getChild(""));
    }

    @Test
    public void testEmptyPathToEmptyPath() {
        assertThat(PathFragment.create("/").getPathString()).isEqualTo("/");
        assertThat(PathFragment.create("").getPathString()).isEqualTo("");
    }

    @Test
    public void testRedundantSlashes() {
        assertThat(PathFragment.create("///").getPathString()).isEqualTo("/");
        assertThat(PathFragment.create("/foo///bar").getPathString()).isEqualTo("/foo/bar");
        assertThat(PathFragment.create("////foo//bar").getPathString()).isEqualTo("/foo/bar");
    }

    @Test
    public void testSimpleNameToSimpleName() {
        assertThat(PathFragment.create("/foo").getPathString()).isEqualTo("/foo");
        assertThat(PathFragment.create("foo").getPathString()).isEqualTo("foo");
    }

    @Test
    public void testSimplePathToSimplePath() {
        assertThat(PathFragment.create("/foo/bar").getPathString()).isEqualTo("/foo/bar");
        assertThat(PathFragment.create("foo/bar").getPathString()).isEqualTo("foo/bar");
    }

    @Test
    public void testStripsTrailingSlash() {
        assertThat(PathFragment.create("/foo/bar/").getPathString()).isEqualTo("/foo/bar");
    }

    @Test
    public void testGetParentDirectory() {
        PathFragment fooBarWiz = PathFragment.create("foo/bar/wiz");
        PathFragment fooBar = PathFragment.create("foo/bar");
        PathFragment foo = PathFragment.create("foo");
        PathFragment empty = PathFragment.create("");
        assertThat(fooBarWiz.getParentDirectory()).isEqualTo(fooBar);
        assertThat(fooBar.getParentDirectory()).isEqualTo(foo);
        assertThat(foo.getParentDirectory()).isEqualTo(empty);
        assertThat(empty.getParentDirectory()).isNull();
        PathFragment fooBarWizAbs = PathFragment.create("/foo/bar/wiz");
        PathFragment fooBarAbs = PathFragment.create("/foo/bar");
        PathFragment fooAbs = PathFragment.create("/foo");
        PathFragment rootAbs = PathFragment.create("/");
        assertThat(fooBarWizAbs.getParentDirectory()).isEqualTo(fooBarAbs);
        assertThat(fooBarAbs.getParentDirectory()).isEqualTo(fooAbs);
        assertThat(fooAbs.getParentDirectory()).isEqualTo(rootAbs);
        assertThat(rootAbs.getParentDirectory()).isNull();
    }

    @Test
    public void testSegmentsCount() {
        assertThat(PathFragment.create("foo/bar").segmentCount()).isEqualTo(2);
        assertThat(PathFragment.create("/foo/bar").segmentCount()).isEqualTo(2);
        assertThat(PathFragment.create("foo//bar").segmentCount()).isEqualTo(2);
        assertThat(PathFragment.create("/foo//bar").segmentCount()).isEqualTo(2);
        assertThat(PathFragment.create("foo/").segmentCount()).isEqualTo(1);
        assertThat(PathFragment.create("/foo/").segmentCount()).isEqualTo(1);
        assertThat(PathFragment.create("foo").segmentCount()).isEqualTo(1);
        assertThat(PathFragment.create("/foo").segmentCount()).isEqualTo(1);
        assertThat(PathFragment.create("/").segmentCount()).isEqualTo(0);
        assertThat(PathFragment.create("").segmentCount()).isEqualTo(0);
    }

    @Test
    public void testGetSegment() {
        assertThat(PathFragment.create("foo/bar").getSegment(0)).isEqualTo("foo");
        assertThat(PathFragment.create("foo/bar").getSegment(1)).isEqualTo("bar");
        assertThat(PathFragment.create("/foo/bar").getSegment(0)).isEqualTo("foo");
        assertThat(PathFragment.create("/foo/bar").getSegment(1)).isEqualTo("bar");
        assertThat(PathFragment.create("foo/").getSegment(0)).isEqualTo("foo");
        assertThat(PathFragment.create("/foo/").getSegment(0)).isEqualTo("foo");
        assertThat(PathFragment.create("foo").getSegment(0)).isEqualTo("foo");
        assertThat(PathFragment.create("/foo").getSegment(0)).isEqualTo("foo");
    }

    @Test
    public void testBasename() throws Exception {
        assertThat(PathFragment.create("foo/bar").getBaseName()).isEqualTo("bar");
        assertThat(PathFragment.create("/foo/bar").getBaseName()).isEqualTo("bar");
        assertThat(PathFragment.create("foo/").getBaseName()).isEqualTo("foo");
        assertThat(PathFragment.create("/foo/").getBaseName()).isEqualTo("foo");
        assertThat(PathFragment.create("foo").getBaseName()).isEqualTo("foo");
        assertThat(PathFragment.create("/foo").getBaseName()).isEqualTo("foo");
        assertThat(PathFragment.create("/").getBaseName()).isEmpty();
        assertThat(PathFragment.create("").getBaseName()).isEmpty();
    }

    @Test
    public void testFileExtension() throws Exception {
        assertThat(PathFragment.create("foo.bar").getFileExtension()).isEqualTo("bar");
        assertThat(PathFragment.create("foo.barr").getFileExtension()).isEqualTo("barr");
        assertThat(PathFragment.create("foo.b").getFileExtension()).isEqualTo("b");
        assertThat(PathFragment.create("foo.").getFileExtension()).isEmpty();
        assertThat(PathFragment.create("foo").getFileExtension()).isEmpty();
        assertThat(PathFragment.create(".").getFileExtension()).isEmpty();
        assertThat(PathFragment.create("").getFileExtension()).isEmpty();
        assertThat(PathFragment.create("foo/bar.baz").getFileExtension()).isEqualTo("baz");
        assertThat(PathFragment.create("foo.bar.baz").getFileExtension()).isEqualTo("baz");
        assertThat(PathFragment.create("foo.bar/baz").getFileExtension()).isEmpty();
    }

    @Test
    public void testReplaceName() throws Exception {
        assertThat(PathFragment.create("foo/bar").replaceName("baz").getPathString()).isEqualTo("foo/baz");
        assertThat(PathFragment.create("/foo/bar").replaceName("baz").getPathString()).isEqualTo("/foo/baz");
        assertThat(PathFragment.create("foo/bar").replaceName("").getPathString()).isEqualTo("foo");
        assertThat(PathFragment.create("foo/").replaceName("baz").getPathString()).isEqualTo("baz");
        assertThat(PathFragment.create("/foo/").replaceName("baz").getPathString()).isEqualTo("/baz");
        assertThat(PathFragment.create("foo").replaceName("baz").getPathString()).isEqualTo("baz");
        assertThat(PathFragment.create("/foo").replaceName("baz").getPathString()).isEqualTo("/baz");
        assertThat(PathFragment.create("/").replaceName("baz")).isNull();
        assertThat(PathFragment.create("/").replaceName("")).isNull();
        assertThat(PathFragment.create("").replaceName("baz")).isNull();
        assertThat(PathFragment.create("").replaceName("")).isNull();
        assertThat(PathFragment.create("foo/bar").replaceName("bar/baz").getPathString()).isEqualTo("foo/bar/baz");
        assertThat(PathFragment.create("foo/bar").replaceName("bar/baz/").getPathString()).isEqualTo("foo/bar/baz");
        // Absolute path arguments will clobber the original path.
        assertThat(PathFragment.create("foo/bar").replaceName("/absolute").getPathString()).isEqualTo("/absolute");
        assertThat(PathFragment.create("foo/bar").replaceName("/").getPathString()).isEqualTo("/");
    }

    @Test
    public void testSubFragment() throws Exception {
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(0, 3).getPathString()).isEqualTo("/foo/bar/baz");
        assertThat(PathFragment.create("foo/bar/baz").subFragment(0, 3).getPathString()).isEqualTo("foo/bar/baz");
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(0, 2).getPathString()).isEqualTo("/foo/bar");
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(1, 3).getPathString()).isEqualTo("bar/baz");
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(0, 1).getPathString()).isEqualTo("/foo");
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(1, 2).getPathString()).isEqualTo("bar");
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(2, 3).getPathString()).isEqualTo("baz");
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(0, 0).getPathString()).isEqualTo("/");
        assertThat(PathFragment.create("foo/bar/baz").subFragment(0, 0).getPathString()).isEqualTo("");
        assertThat(PathFragment.create("foo/bar/baz").subFragment(1, 1).getPathString()).isEqualTo("");
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(0).getPathString()).isEqualTo("/foo/bar/baz");
        assertThat(PathFragment.create("foo/bar/baz").subFragment(0).getPathString()).isEqualTo("foo/bar/baz");
        assertThat(PathFragment.create("/foo/bar/baz").subFragment(1).getPathString()).isEqualTo("bar/baz");
        assertThat(PathFragment.create("foo/bar/baz").subFragment(1).getPathString()).isEqualTo("bar/baz");
        assertThat(PathFragment.create("foo/bar/baz").subFragment(2).getPathString()).isEqualTo("baz");
        assertThat(PathFragment.create("foo/bar/baz").subFragment(3).getPathString()).isEqualTo("");
        MoreAsserts.assertThrows(IndexOutOfBoundsException.class, () -> PathFragment.create("foo/bar/baz").subFragment(3, 2));
        MoreAsserts.assertThrows(IndexOutOfBoundsException.class, () -> PathFragment.create("foo/bar/baz").subFragment(4, 4));
        MoreAsserts.assertThrows(IndexOutOfBoundsException.class, () -> PathFragment.create("foo/bar/baz").subFragment(3, 2));
        MoreAsserts.assertThrows(IndexOutOfBoundsException.class, () -> PathFragment.create("foo/bar/baz").subFragment(4));
    }

    @Test
    public void testStartsWith() {
        PathFragment foobar = PathFragment.create("/foo/bar");
        PathFragment foobarRelative = PathFragment.create("foo/bar");
        // (path, prefix) => true
        assertThat(foobar.startsWith(foobar)).isTrue();
        assertThat(foobar.startsWith(PathFragment.create("/"))).isTrue();
        assertThat(foobar.startsWith(PathFragment.create("/foo"))).isTrue();
        assertThat(foobar.startsWith(PathFragment.create("/foo/"))).isTrue();
        assertThat(foobar.startsWith(PathFragment.create("/foo/bar/"))).isTrue();// Includes trailing slash.

        // (prefix, path) => false
        assertThat(PathFragment.create("/foo").startsWith(foobar)).isFalse();
        assertThat(PathFragment.create("/").startsWith(foobar)).isFalse();
        // (absolute, relative) => false
        assertThat(foobar.startsWith(foobarRelative)).isFalse();
        assertThat(foobarRelative.startsWith(foobar)).isFalse();
        // (relative path, relative prefix) => true
        assertThat(foobarRelative.startsWith(foobarRelative)).isTrue();
        assertThat(foobarRelative.startsWith(PathFragment.create("foo"))).isTrue();
        assertThat(foobarRelative.startsWith(PathFragment.create(""))).isTrue();
        // (path, sibling) => false
        assertThat(PathFragment.create("/foo/wiz").startsWith(foobar)).isFalse();
        assertThat(foobar.startsWith(PathFragment.create("/foo/wiz"))).isFalse();
    }

    @Test
    public void testCheckAllPathsStartWithButAreNotEqualTo() {
        // Check passes:
        PathFragment.checkAllPathsAreUnder(PathFragmentTest.toPathsSet("a/b", "a/c"), PathFragment.create("a"));
        // Check trivially passes:
        PathFragment.checkAllPathsAreUnder(ImmutableList.<PathFragment>of(), PathFragment.create("a"));
        // Check fails when some path does not start with startingWithPath:
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.checkAllPathsAreUnder(PathFragmentTest.toPathsSet("a/b", "b/c"), PathFragment.create("a")));
        // Check fails when some path is equal to startingWithPath:
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.checkAllPathsAreUnder(PathFragmentTest.toPathsSet("a/b", "a"), PathFragment.create("a")));
    }

    @Test
    public void testEndsWith() {
        PathFragment foobar = PathFragment.create("/foo/bar");
        PathFragment foobarRelative = PathFragment.create("foo/bar");
        // (path, suffix) => true
        assertThat(foobar.endsWith(foobar)).isTrue();
        assertThat(foobar.endsWith(PathFragment.create("bar"))).isTrue();
        assertThat(foobar.endsWith(PathFragment.create("foo/bar"))).isTrue();
        assertThat(foobar.endsWith(PathFragment.create("/foo/bar"))).isTrue();
        assertThat(foobar.endsWith(PathFragment.create("/bar"))).isFalse();
        // (prefix, path) => false
        assertThat(PathFragment.create("/foo").endsWith(foobar)).isFalse();
        assertThat(PathFragment.create("/").endsWith(foobar)).isFalse();
        // (suffix, path) => false
        assertThat(PathFragment.create("/bar").endsWith(foobar)).isFalse();
        assertThat(PathFragment.create("bar").endsWith(foobar)).isFalse();
        assertThat(PathFragment.create("").endsWith(foobar)).isFalse();
        // (absolute, relative) => true
        assertThat(foobar.endsWith(foobarRelative)).isTrue();
        // (relative, absolute) => false
        assertThat(foobarRelative.endsWith(foobar)).isFalse();
        // (relative path, relative prefix) => true
        assertThat(foobarRelative.endsWith(foobarRelative)).isTrue();
        assertThat(foobarRelative.endsWith(PathFragment.create("bar"))).isTrue();
        assertThat(foobarRelative.endsWith(PathFragment.create(""))).isTrue();
        // (path, sibling) => false
        assertThat(PathFragment.create("/foo/wiz").endsWith(foobar)).isFalse();
        assertThat(foobar.endsWith(PathFragment.create("/foo/wiz"))).isFalse();
    }

    @Test
    public void testToRelative() {
        assertThat(PathFragment.create("/foo/bar").toRelative()).isEqualTo(PathFragment.create("foo/bar"));
        assertThat(PathFragment.create("/").toRelative()).isEqualTo(PathFragment.create(""));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("foo").toRelative());
    }

    @Test
    public void testGetDriveStr() {
        assertThat(PathFragment.create("/foo/bar").getDriveStr()).isEqualTo("/");
        assertThat(PathFragment.create("/").getDriveStr()).isEqualTo("/");
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("foo").getDriveStr());
    }

    @Test
    public void testCompareTo() throws Exception {
        List<String> pathStrs = ImmutableList.of("", "/", "foo", "/foo", "foo/bar", "foo.bar", "foo/bar.baz", "foo/bar/baz", "foo/barfile", "foo/Bar", "Foo/bar");
        List<PathFragment> paths = PathFragmentTest.toPaths(pathStrs);
        // First test that compareTo is self-consistent.
        for (PathFragment x : paths) {
            for (PathFragment y : paths) {
                for (PathFragment z : paths) {
                    // Anti-symmetry
                    assertThat(((-1) * (Integer.signum(y.compareTo(x))))).isEqualTo(Integer.signum(x.compareTo(y)));
                    // Transitivity
                    if (((x.compareTo(y)) > 0) && ((y.compareTo(z)) > 0)) {
                        assertThat(x.compareTo(z)).isGreaterThan(0);
                    }
                    // "Substitutability"
                    if ((x.compareTo(y)) == 0) {
                        assertThat(Integer.signum(y.compareTo(z))).isEqualTo(Integer.signum(x.compareTo(z)));
                    }
                    // Consistency with equals
                    assertThat(x.equals(y)).isEqualTo(((x.compareTo(y)) == 0));
                }
            }
        }
        // Now test that compareTo does what we expect.  The exact ordering here doesn't matter much.
        Collections.shuffle(paths);
        Collections.sort(paths);
        List<PathFragment> expectedOrder = PathFragmentTest.toPaths(ImmutableList.of("", "/", "/foo", "Foo/bar", "foo", "foo.bar", "foo/Bar", "foo/bar", "foo/bar.baz", "foo/bar/baz", "foo/barfile"));
        assertThat(paths).isEqualTo(expectedOrder);
    }

    @Test
    public void testGetSafePathString() {
        assertThat(PathFragment.create("/").getSafePathString()).isEqualTo("/");
        assertThat(PathFragment.create("/abc").getSafePathString()).isEqualTo("/abc");
        assertThat(PathFragment.create("").getSafePathString()).isEqualTo(".");
        assertThat(EMPTY_FRAGMENT.getSafePathString()).isEqualTo(".");
        assertThat(PathFragment.create("abc/def").getSafePathString()).isEqualTo("abc/def");
    }

    @Test
    public void testNormalize() {
        assertThat(PathFragment.create("/a/b")).isEqualTo(PathFragment.create("/a/b"));
        assertThat(PathFragment.create("/a/./b")).isEqualTo(PathFragment.create("/a/b"));
        assertThat(PathFragment.create("/a/../b")).isEqualTo(PathFragment.create("/b"));
        assertThat(PathFragment.create("a/b")).isEqualTo(PathFragment.create("a/b"));
        assertThat(PathFragment.create("a/../../b")).isEqualTo(PathFragment.create("../b"));
        assertThat(PathFragment.create("a/../..")).isEqualTo(PathFragment.create(".."));
        assertThat(PathFragment.create("a/../b")).isEqualTo(PathFragment.create("b"));
        assertThat(PathFragment.create("a/b/../b")).isEqualTo(PathFragment.create("a/b"));
        assertThat(PathFragment.create("/..")).isEqualTo(PathFragment.create("/.."));
        assertThat(PathFragment.create("..")).isEqualTo(PathFragment.create(".."));
    }

    @Test
    public void testSegments() {
        assertThat(PathFragment.create("").segmentCount()).isEqualTo(0);
        assertThat(PathFragment.create("a").segmentCount()).isEqualTo(1);
        assertThat(PathFragment.create("a/b").segmentCount()).isEqualTo(2);
        assertThat(PathFragment.create("a/b/c").segmentCount()).isEqualTo(3);
        assertThat(PathFragment.create("/").segmentCount()).isEqualTo(0);
        assertThat(PathFragment.create("/a").segmentCount()).isEqualTo(1);
        assertThat(PathFragment.create("/a/b").segmentCount()).isEqualTo(2);
        assertThat(PathFragment.create("/a/b/c").segmentCount()).isEqualTo(3);
        assertThat(PathFragment.create("").getSegments()).isEmpty();
        assertThat(PathFragment.create("a").getSegments()).containsExactly("a").inOrder();
        assertThat(PathFragment.create("a/b").getSegments()).containsExactly("a", "b").inOrder();
        assertThat(PathFragment.create("a/b/c").getSegments()).containsExactly("a", "b", "c").inOrder();
        assertThat(PathFragment.create("/").getSegments()).isEmpty();
        assertThat(PathFragment.create("/a").getSegments()).containsExactly("a").inOrder();
        assertThat(PathFragment.create("/a/b").getSegments()).containsExactly("a", "b").inOrder();
        assertThat(PathFragment.create("/a/b/c").getSegments()).containsExactly("a", "b", "c").inOrder();
        assertThat(PathFragment.create("a").getSegment(0)).isEqualTo("a");
        assertThat(PathFragment.create("a/b").getSegment(0)).isEqualTo("a");
        assertThat(PathFragment.create("a/b").getSegment(1)).isEqualTo("b");
        assertThat(PathFragment.create("a/b/c").getSegment(2)).isEqualTo("c");
        assertThat(PathFragment.create("/a").getSegment(0)).isEqualTo("a");
        assertThat(PathFragment.create("/a/b").getSegment(0)).isEqualTo("a");
        assertThat(PathFragment.create("/a/b").getSegment(1)).isEqualTo("b");
        assertThat(PathFragment.create("/a/b/c").getSegment(2)).isEqualTo("c");
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("").getSegment(0));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("a/b").getSegment(2));
    }

    @Test
    public void testCodec() throws Exception {
        runTests();
    }

    @Test
    public void testSerializationSimple() throws Exception {
        checkSerialization("a", 91);
    }

    @Test
    public void testSerializationAbsolute() throws Exception {
        checkSerialization("/foo", 94);
    }

    @Test
    public void testSerializationNested() throws Exception {
        checkSerialization("foo/bar/baz", 101);
    }
}

