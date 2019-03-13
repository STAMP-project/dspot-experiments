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


import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * This class tests the functionality of the PathFragment.
 */
@RunWith(JUnit4.class)
public class PathFragmentWindowsTest {
    @Test
    public void testWindowsSeparator() {
        assertThat(PathFragment.create("bar\\baz").toString()).isEqualTo("bar/baz");
        assertThat(PathFragment.create("c:\\bar\\baz").toString()).isEqualTo("C:/bar/baz");
    }

    @Test
    public void testIsAbsoluteWindows() {
        assertThat(PathFragment.create("C:/").isAbsolute()).isTrue();
        assertThat(PathFragment.create("C:/").isAbsolute()).isTrue();
        assertThat(PathFragment.create("C:/foo").isAbsolute()).isTrue();
        assertThat(PathFragment.create("d:/foo/bar").isAbsolute()).isTrue();
        assertThat(PathFragment.create("*:/").isAbsolute()).isFalse();
    }

    @Test
    public void testAbsoluteAndAbsoluteLookingPaths() {
        assertThat(PathFragment.create("/c").isAbsolute()).isTrue();
        assertThat(PathFragment.create("/c").getSegments()).containsExactly("c");
        assertThat(PathFragment.create("/c/").isAbsolute()).isTrue();
        assertThat(PathFragment.create("/c/").getSegments()).containsExactly("c");
        assertThat(PathFragment.create("C:/").isAbsolute()).isTrue();
        assertThat(PathFragment.create("C:/").getSegments()).isEmpty();
        PathFragment p5 = PathFragment.create("/c:");
        assertThat(p5.isAbsolute()).isTrue();
        assertThat(p5.getSegments()).containsExactly("c:");
        assertThat(PathFragment.create("C:").isAbsolute()).isFalse();
        assertThat(PathFragment.create("/c:").isAbsolute()).isTrue();
        assertThat(PathFragment.create("/c:").getSegments()).containsExactly("c:");
        assertThat(PathFragment.create("/c")).isEqualTo(PathFragment.create("/c/"));
        assertThat(PathFragment.create("/c")).isNotEqualTo(PathFragment.create("C:/"));
        assertThat(PathFragment.create("/c")).isNotEqualTo(PathFragment.create("C:"));
        assertThat(PathFragment.create("/c")).isNotEqualTo(PathFragment.create("/c:"));
        assertThat(PathFragment.create("C:/")).isNotEqualTo(PathFragment.create("C:"));
        assertThat(PathFragment.create("C:/")).isNotEqualTo(PathFragment.create("/c:"));
    }

    @Test
    public void testIsAbsoluteWindowsBackslash() {
        assertThat(PathFragment.create(new File("C:\\blah").getPath()).isAbsolute()).isTrue();
        assertThat(PathFragment.create(new File("C:\\").getPath()).isAbsolute()).isTrue();
        assertThat(PathFragment.create(new File("\\blah").getPath()).isAbsolute()).isTrue();
        assertThat(PathFragment.create(new File("\\").getPath()).isAbsolute()).isTrue();
    }

    @Test
    public void testRootNodeReturnsRootStringWindows() {
        assertThat(PathFragment.create("C:/").getPathString()).isEqualTo("C:/");
    }

    @Test
    public void testGetRelativeWindows() {
        assertThat(PathFragment.create("C:/a").getRelative("b").getPathString()).isEqualTo("C:/a/b");
        assertThat(PathFragment.create("C:/a/b").getRelative("c/d").getPathString()).isEqualTo("C:/a/b/c/d");
        assertThat(PathFragment.create("C:/a").getRelative("C:/b").getPathString()).isEqualTo("C:/b");
        assertThat(PathFragment.create("C:/a/b").getRelative("C:/c/d").getPathString()).isEqualTo("C:/c/d");
        assertThat(PathFragment.create("a").getRelative("C:/b").getPathString()).isEqualTo("C:/b");
        assertThat(PathFragment.create("a/b").getRelative("C:/c/d").getPathString()).isEqualTo("C:/c/d");
    }

    @Test
    public void testGetRelativeMixed() throws Exception {
        assertThat(PathFragment.create("a").getRelative("b")).isEqualTo(PathFragment.create("a/b"));
        assertThat(PathFragment.create("a").getRelative("/b")).isEqualTo(PathFragment.create("/b"));
        assertThat(PathFragment.create("a").getRelative("E:/b")).isEqualTo(PathFragment.create("E:/b"));
        assertThat(PathFragment.create("/a").getRelative("b")).isEqualTo(PathFragment.create("/a/b"));
        assertThat(PathFragment.create("/a").getRelative("/b")).isEqualTo(PathFragment.create("/b"));
        assertThat(PathFragment.create("/a").getRelative("E:/b")).isEqualTo(PathFragment.create("E:/b"));
        assertThat(PathFragment.create("D:/a").getRelative("b")).isEqualTo(PathFragment.create("D:/a/b"));
        assertThat(PathFragment.create("D:/a").getRelative("/b")).isEqualTo(PathFragment.create("/b"));
        assertThat(PathFragment.create("D:/a").getRelative("E:/b")).isEqualTo(PathFragment.create("E:/b"));
    }

    @Test
    public void testRelativeTo() throws Exception {
        assertThat(PathFragment.create("").relativeTo("").getPathString()).isEqualTo("");
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("").relativeTo("a"));
        assertThat(PathFragment.create("a").relativeTo("")).isEqualTo(PathFragment.create("a"));
        assertThat(PathFragment.create("a").relativeTo("a").getPathString()).isEqualTo("");
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("a").relativeTo("b"));
        assertThat(PathFragment.create("a/b").relativeTo("a")).isEqualTo(PathFragment.create("b"));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("C:/").relativeTo(""));
        assertThat(PathFragment.create("C:/").relativeTo("C:/").getPathString()).isEqualTo("");
    }

    @Test
    public void testGetChildWorks() {
        assertThat(PathFragment.create("../some/path").getChild("hi")).isEqualTo(PathFragment.create("../some/path/hi"));
    }

    @Test
    public void testEmptyPathToEmptyPathWindows() {
        assertThat(PathFragment.create("C:/")).isEqualTo(PathFragment.create("C:/"));
    }

    @Test
    public void testWindowsVolumeUppercase() {
        assertThat(PathFragment.create("C:/")).isEqualTo(PathFragment.create("c:/"));
    }

    @Test
    public void testRedundantSlashesWindows() {
        assertThat(PathFragment.create("C:/")).isEqualTo(PathFragment.create("C:///"));
        assertThat(PathFragment.create("C:/foo/bar")).isEqualTo(PathFragment.create("C:/foo///bar"));
        assertThat(PathFragment.create("C:/foo/bar")).isEqualTo(PathFragment.create("C:////foo//bar"));
    }

    @Test
    public void testSimpleNameToSimpleNameWindows() {
        assertThat(PathFragment.create("C:/foo")).isEqualTo(PathFragment.create("C:/foo"));
    }

    @Test
    public void testStripsTrailingSlashWindows() {
        assertThat(PathFragment.create("C:/foo/bar")).isEqualTo(PathFragment.create("C:/foo/bar/"));
    }

    @Test
    public void testGetParentDirectoryWindows() {
        assertThat(PathFragment.create("C:/foo/bar/wiz").getParentDirectory()).isEqualTo(PathFragment.create("C:/foo/bar"));
        assertThat(PathFragment.create("C:/foo/bar").getParentDirectory()).isEqualTo(PathFragment.create("C:/foo"));
        assertThat(PathFragment.create("C:/foo").getParentDirectory()).isEqualTo(PathFragment.create("C:/"));
        assertThat(PathFragment.create("C:/").getParentDirectory()).isNull();
    }

    @Test
    public void testSegmentsCountWindows() {
        assertThat(PathFragment.create("C:/foo").segmentCount()).isEqualTo(1);
        assertThat(PathFragment.create("C:/").segmentCount()).isEqualTo(0);
    }

    @Test
    public void testGetSegmentWindows() {
        assertThat(PathFragment.create("C:/foo/bar").getSegment(0)).isEqualTo("foo");
        assertThat(PathFragment.create("C:/foo/bar").getSegment(1)).isEqualTo("bar");
        assertThat(PathFragment.create("C:/foo/").getSegment(0)).isEqualTo("foo");
        assertThat(PathFragment.create("C:/foo").getSegment(0)).isEqualTo("foo");
    }

    @Test
    public void testBasenameWindows() throws Exception {
        assertThat(PathFragment.create("C:/foo/bar").getBaseName()).isEqualTo("bar");
        assertThat(PathFragment.create("C:/foo").getBaseName()).isEqualTo("foo");
        // Never return the drive name as a basename.
        assertThat(PathFragment.create("C:/").getBaseName()).isEmpty();
    }

    @Test
    public void testReplaceNameWindows() throws Exception {
        assertThat(PathFragment.create("C:/foo/bar").replaceName("baz").getPathString()).isEqualTo("C:/foo/baz");
        assertThat(PathFragment.create("C:/").replaceName("baz")).isNull();
    }

    @Test
    public void testStartsWithWindows() {
        assertThat(PathFragment.create("C:/foo/bar").startsWith(PathFragment.create("C:/foo"))).isTrue();
        assertThat(PathFragment.create("C:/foo/bar").startsWith(PathFragment.create("C:/"))).isTrue();
        assertThat(PathFragment.create("C:/").startsWith(PathFragment.create("C:/"))).isTrue();
        // The first path is absolute, the second is not.
        assertThat(PathFragment.create("C:/foo/bar").startsWith(PathFragment.create("C:"))).isFalse();
        assertThat(PathFragment.create("C:/").startsWith(PathFragment.create("C:"))).isFalse();
    }

    @Test
    public void testEndsWithWindows() {
        assertThat(PathFragment.create("C:/foo/bar").endsWith(PathFragment.create("bar"))).isTrue();
        assertThat(PathFragment.create("C:/foo/bar").endsWith(PathFragment.create("foo/bar"))).isTrue();
        assertThat(PathFragment.create("C:/foo/bar").endsWith(PathFragment.create("C:/foo/bar"))).isTrue();
        assertThat(PathFragment.create("C:/").endsWith(PathFragment.create("C:/"))).isTrue();
    }

    @Test
    public void testGetSafePathStringWindows() {
        assertThat(PathFragment.create("C:/").getSafePathString()).isEqualTo("C:/");
        assertThat(PathFragment.create("C:/abc").getSafePathString()).isEqualTo("C:/abc");
        assertThat(PathFragment.create("C:/abc/def").getSafePathString()).isEqualTo("C:/abc/def");
    }

    @Test
    public void testNormalizeWindows() {
        assertThat(PathFragment.create("C:/a/b")).isEqualTo(PathFragment.create("C:/a/b"));
        assertThat(PathFragment.create("C:/a/./b")).isEqualTo(PathFragment.create("C:/a/b"));
        assertThat(PathFragment.create("C:/a/../b")).isEqualTo(PathFragment.create("C:/b"));
        assertThat(PathFragment.create("C:/../b")).isEqualTo(PathFragment.create("C:/../b"));
    }

    @Test
    public void testWindowsDriveRelativePaths() throws Exception {
        // On Windows, paths that look like "C:foo" mean "foo relative to the current directory
        // of drive C:\".
        // Bazel doesn't resolve such paths, and just takes them literally like normal path segments.
        // If the user attempts to open files under such paths, the file system API will give an error.
        assertThat(PathFragment.create("C:").isAbsolute()).isFalse();
        assertThat(PathFragment.create("C:").getSegments()).containsExactly("C:");
    }

    @Test
    public void testToRelative() {
        assertThat(PathFragment.create("C:/foo/bar").toRelative()).isEqualTo(PathFragment.create("foo/bar"));
        assertThat(PathFragment.create("C:/").toRelative()).isEqualTo(PathFragment.create(""));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("foo").toRelative());
    }

    @Test
    public void testGetDriveStr() {
        assertThat(PathFragment.create("C:/foo/bar").getDriveStr()).isEqualTo("C:/");
        assertThat(PathFragment.create("C:/").getDriveStr()).isEqualTo("C:/");
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> PathFragment.create("foo").getDriveStr());
    }
}

